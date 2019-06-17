package com.example.preschool.Chats;


import com.example.preschool.Notifications.MyResponse;
import com.example.preschool.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAM9b6Sh4:APA91bGrbqpHH5p2Fm-Bo7D1PnIY_cVVuPwo9DH3dhYTkzChiDV-MeeXYj0Q78MiQ-fiymqE64XR3hDYaqY54KqMfOlDaI6XsFJDi9T15UBGqb2Wd6eJVXkeKoP5gnpApnJ9UPkP2Ksz"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
