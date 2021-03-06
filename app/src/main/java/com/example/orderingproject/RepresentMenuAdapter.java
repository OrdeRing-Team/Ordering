package com.example.orderingproject;

import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderingproject.Dialog.CustomDialog;
import com.example.orderingproject.Dialog.CustomMenuOptionDialog;
import com.example.orderingproject.Dialog.CustomMenuOptionDialogListener;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.BasketRequestDto;
import com.example.orderingproject.Dto.response.RepresentativeMenuDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RepresentMenuAdapter extends RecyclerView.Adapter<RepresentMenuAdapter.CustomViewHolder> {
    List<RepresentativeMenuDto> arrayList;
    Context context;
    public CustomMenuOptionDialog dialog;

    public CustomDialog errorDialog;

    View.OnClickListener positiveButton;
    View.OnClickListener negativeButton;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        //        adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
        //        itemView를 저장하는 custom viewHolder 생성
        //        findViewById & 각종 event 작업
        TextView tvName, tvPrice, tvIntro, tvSoldout, tvRepresent;
        ImageView ivMenu;
        ConstraintLayout clBaseLayout;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정

            tvName = itemView.findViewById(R.id.item_name);
            tvPrice = itemView.findViewById(R.id.item_price);
            tvIntro = itemView.findViewById(R.id.item_intro);
            ivMenu = itemView.findViewById(R.id.item_image);
            tvSoldout = itemView.findViewById(R.id.item_soldout);
            clBaseLayout = itemView.findViewById(R.id.cl_baselayout);
            tvRepresent = itemView.findViewById(R.id.tv_represent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Log.e("menu", "item clicked.");
                    }
                }
            });
        }
    }

    public RepresentMenuAdapter(List<RepresentativeMenuDto> arrayList) {
    // adapter constructor
        this.arrayList = arrayList;
    }


    public RepresentMenuAdapter(List<RepresentativeMenuDto> arrayList, Context context) {
    // adapter constructor for needing context part
        this.arrayList = arrayList;
        this.context = context;
    }




    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    // onCreateViewHolder: make xml as an object using LayoutInflater & create viewHolder with the object
    // layoutInflater로 xml객체화. viewHolder 생성.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_represent, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
    // onBindViewHolder: put data of item list into xml widgets
    // xml의 위젯과 데이터를 묶는(연결하는, setting하는) 작업.
    // position에 해당하는 data, viewHolder의 itemView에 표시함

        holder.tvName.setText(arrayList.get(position).getFoodName());
        holder.tvPrice.setText(Utillity.computePrice(arrayList.get(position).getPrice()));
        holder.tvIntro.setText(String.valueOf(arrayList.get(position).getMenuIntro()));

        // arrayList에 저장된 메뉴 이미지 url을 imageURL변수에 저장하고 Glide로 iv에 set
        String imageURL = String.valueOf(arrayList.get(position).getImageUrl());
        Glide.with(holder.itemView.getContext()).load(imageURL).into(holder.ivMenu);

        // 둥근 모서리 이미지뷰에서는 scaleType 변경 시 둥근 모서리가 해제됨. 따라서 코드상에서 다시 설정 해준다.
        // 아래 메서드는 API 21 이상부터 사용 가능
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            holder.ivMenu.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0,0,view.getWidth(),view.getHeight(),40);
                }
            });

            holder.ivMenu.setClipToOutline(true);
        }

        // 품절 정보 불러와서 true이면 리사이클러뷰에 "품절" 출력하기
        boolean soldout = arrayList.get(position).getSoldOut();
        if (soldout) {
            holder.tvSoldout.setVisibility(View.VISIBLE);
            holder.tvSoldout.setText("품절");
            holder.itemView.setClickable(false);
            holder.clBaseLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_gray));
        }

        // MenuActivity에서 전역변수 fromTo에 저장된 값이 fav, waiting일 경우 해당 뷰에서 이동되었으므로 아이템 클릭 불가하도록 설정.
        if (MenuActivity.fromTo.equals("favActivity") || MenuActivity.fromTo.equals("waitingFrag")) {
            holder.itemView.setClickable(false);
        }

        if(holder.itemView.isClickable()) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog = new CustomMenuOptionDialog(view.getContext(),
                            arrayList.get(position).getFoodName(),
                            String.valueOf(arrayList.get(position).getMenuIntro()),
                            imageURL,
                            String.valueOf(arrayList.get(position).getPrice()),
                            arrayList.get(position).getFoodId());
                    dialog.setDialogListener(new CustomMenuOptionDialogListener() {
                        @Override
                        public void onAddBasketButtonClicked(Long foodId, int totalPrice, int totalCount) {
                            addOnBasket(foodId, totalPrice, totalCount, holder);
                            Log.e("onAddBasketButtonClicked","실행");
                        }
                    });
                    dialog.show();

                }
            });
        }
        positiveButton = view -> {

            errorDialog.showProgress();
            try {
                new Thread() {
                    @SneakyThrows
                    public void run() {
                        String url = "http://www.ordering.ml/";

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(url)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        RetrofitService service = retrofit.create(RetrofitService.class);
                        Call<ResultDto<Boolean>> call =
                                service.clearBasket(UserInfo.getCustomerId());

                        call.enqueue(new Callback<ResultDto<Boolean>>() {
                            @Override
                            public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                                if (response.isSuccessful()) {

                                    ResultDto<Boolean> result;
                                    result = response.body();
                                    if (result.getData()) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                UserInfo.setBasketCount(0);
                                                MenuActivity.updateBasket();
                                                addOnBasket(errorDialog.getFoodId(), errorDialog.getTotalPrice(), errorDialog.getTotalCount(), holder);
                                                errorDialog.dismiss();
                                            }
                                        });
                                        Log.e("result.getData() ", Boolean.toString(result.getData()));
                                    }else{
                                        Log.e("result.getData() ", "false");
                                        errorDialog.dismiss();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                                Toast.makeText(holder.itemView.getContext(), "장바구니를 초기화 하는 중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                Log.e("e = ", t.getMessage());
                                errorDialog.dismiss();
                            }
                        });
                    }
                }.start();

            } catch (Exception e) {
                Toast.makeText(holder.itemView.getContext(), "장바구니를 초기화 하는 중 일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                Log.e("e = ", e.getMessage());
                errorDialog.dismiss();
            }
        };

        negativeButton = view -> {
            errorDialog.dismiss();
        };
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public void deleteItem(int position) {
        /* view에서 삭제 */
        arrayList.remove(position);
        notifyDataSetChanged();
    }

    private void addOnBasket(Long foodId, int totalPrice, int totalCount, RepresentMenuAdapter.CustomViewHolder holder){
        try {
            new Thread() {
                @SneakyThrows
                public void run() {
                    Log.e("장바구니 담기 버튼", "클릭");
                    String url = "http://www.ordering.ml/";

                    BasketRequestDto basketDto = new BasketRequestDto(foodId, totalPrice, totalCount);
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.addBasket(UserInfo.getCustomerId(), Long.valueOf(MenuActivity.store), basketDto);

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<Boolean> result;
                                result = response.body();
                                if (result.getData()) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            UserInfo.addBasketCount(totalCount);
                                            // MenuActivity.setBasketCount(totalCount);
                                            MenuActivity.updateBasket();
                                            Toast.makeText(holder.itemView.getContext(), "장바구니에 메뉴를 추가했습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Log.e("result.getData() ", Boolean.toString(result.getData()));
                                }else{
                                    errorDialog = new CustomDialog(
                                            context,
                                            "현재 장바구니에 다른 매장의 메뉴가 담겨 있습니다.",
                                            "이전에 담긴 메뉴를 삭제하고 새로 담으시겠습니까?",
                                            "담기","취소",
                                            positiveButton,negativeButton, "#000000", foodId, totalPrice, totalCount);

                                    errorDialog.show();

                                }
                            }
                            else{
                                Log.e("response","failed");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            Toast.makeText(holder.itemView.getContext(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(holder.itemView.getContext(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }
}

