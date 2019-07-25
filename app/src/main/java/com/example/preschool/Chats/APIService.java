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
                    "Authorization:key=AAAAz169bsA:APA91bGpTO2HKydUgGScEJMOS4IXFiuiDwt68Y-lVPMfOAzKxwhf3H5-b19vplGEda41gfqzSnC9NdmknCJQN4tDNXkWim6pH6VGPZC9v3H1p-Kg_6jcIsdzVEfE6XJnsnHsbyNtYqQi"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
