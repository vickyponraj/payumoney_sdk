package com.example.payumoney_sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUBillingCycle;
import com.payu.base.models.PayUPaymentParams;
import  com.payu.base.models.PayUSIParams;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.Log;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import io.flutter.plugin.common.PluginRegistry;
import static android.app.Activity.RESULT_OK;





/** PayumoneySdkPlugin */
public class PayumoneySdkPlugin implements FlutterPlugin, MethodCallHandler,PluginRegistry.ActivityResultListener,ActivityAware {
  private MethodChannel channel;
  private MethodChannel.Result mainResult;
  private Activity activity;
  private  Context context;
  private ActivityPluginBinding pluginBinding;
  private static final String TAG = "PayuMoney Flutter Plugin";
  private boolean isProduction = false;
  PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
  PayUPaymentParams payUPaymentParams =null;
  PayUSIParams siDetails=null;





  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "payumoney_sdk");
    channel.setMethodCallHandler(this);



  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    this.mainResult=result;
     if(call.method.equals("buildPaymentParams")){

       buildPaymentParams(call);
     }else if(call.method.equals("setPayuSiParam")){
       setPayuSiParam(call);
    }else{
      result.notImplemented();
    }
  }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data){


    Log.e(TAG,"request code "+requestCode+" result code "+resultCode);
//    if(requestCode==PayUmoneyFlowManager.REQUEST_CODE_PAYMENT&&resultCode==RESULT_OK&&data!=null){
//      TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
//
//      if(transactionResponse!=null&&transactionResponse.getPayuResponse()!=null){
//        if(transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
//
//          HashMap<String, Object> response = new HashMap<String, Object>();
//          response.put("status", "success");
//          response.put("message", transactionResponse.getMessage());
//          mainResult.success(response);
//        } else {
//          HashMap<String, Object> response = new HashMap<String, Object>();
//          response.put("message", transactionResponse.getMessage());
//          response.put("status", "failed");
//
//          mainResult.success(response);
//        }
//      }
//
//    }
    return  false;
  }





  private  void setPayuSiParam(MethodCall call){
   siDetails= new PayUSIParams.Builder()
            .setIsFreeTrial((boolean)call.argument("isFreeTrial")) //set it to true for free trial. Default value is false
            .setBillingAmount((String)call.argument("billingAmt"))
            .setBillingCycle(PayUBillingCycle.valueOf((String)call.argument("billingCycle")))
            .setBillingInterval(Integer.parseInt(((String)call.argument("setBillingInterval"))))
            .setPaymentStartDate((String)call.argument("paymentStartDate"))
            .setPaymentEndDate((String)call.argument("paymentEndDate"))
            .setRemarks((String)call.argument("remarks"))
            .build();
  }


  private void buildPaymentParams(MethodCall call) {
    builder.setAmount((String) call.argument("amount"))
            .setTransactionId((String) call.argument("transactionId"))
            .setPhone((String) call.argument("phone"))
            .setProductInfo((String) call.argument("productInfo"))
            .setFirstName((String) call.argument("firstName"))
            .setEmail((String) call.argument("email"))
            .setSurl((String)call.argument("successURL"))
            .setFurl((String)call.argument("failureURL"))
            .setIsProduction((boolean)call.argument("isProduction"))
            .setKey((String)call.argument("merchantKey"))
            .setUserCredential((String)call.argument("userCredentials"));


    if(siDetails!=null){
      builder.setPayUSIParams(siDetails);
    }



    try {
      this.payUPaymentParams = builder.build();
      startPayment(this.payUPaymentParams,(String)call.argument("hash"));


//      String hashSequence = this.merchantKey +"|"+
//              (String) call.argument("orderID") +"|" +(String) call.argument("amount")+"|" +(String) call.argument("productName")+"|" +
//              (String) call.argument("firstname") +"|" + (String) call.argument("email") +"|||||||||||" +
//              "seVTUgzrgE"
              //"cMDID7EG"

          //    ;
     // String serverCalculatedHash= hashCal("SHA-512", hashSequence);



    } catch (Exception e) {
      mainResult.error("ERROR", e.getMessage(), null);
      Log.d(TAG, "Error : " + e.toString());
    }
  }


  private void startPayment(PayUPaymentParams payUPaymentParams,final String hash){
    PayUCheckoutPro.open(
            this,
            payUPaymentParams,
            new PayUCheckoutProListener() {

              @Override
              public void onPaymentSuccess(Object response) {
                //Cast response object to HashMap
                HashMap<String,Object> result = (HashMap<String, Object>) response;
                String payuResponse = (String)result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);

                mainResult.success(result);

              }

              @Override
              public void onPaymentFailure(Object response) {
                //Cast response object to HashMap
                HashMap<String,Object> result = (HashMap<String, Object>) response;
                String payuResponse = (String)result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);

                mainResult.error("400","Payment failed","Payment failed");
              }

              @Override
              public void onPaymentCancel(boolean isTxnInitiated) {

                mainResult.error("400","Payment failed","Payment failed");
              }

              @Override
              public void onError(ErrorResponse errorResponse) {
                String errorMessage = errorResponse.getErrorMessage();
              }

              @Override
              public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                //For setting webview properties, if any. Check Customized Integration section for more details on this
              }

              @Override
              public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                String hashName =valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                HashMap<String, String> dataMap = new HashMap<>();
                dataMap.put(hashName, hash);
                 hashGenerationListener.onHashGenerated(dataMap);

              }
            }
    );
  }




  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    this.activity = binding.getActivity();
    Log.e("this is a test",String.valueOf(activity));
    this.pluginBinding=binding;
    binding.addActivityResultListener(this);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
   onDetachedFromActivity();

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }


  @Override
  public void onDetachedFromActivity() {
   pluginBinding.removeActivityResultListener(this);
   pluginBinding=null;
  }



  public static String hashCal(String type, String hashString) {
    StringBuilder hash = new StringBuilder();
    MessageDigest messageDigest = null;
    try {
      messageDigest = MessageDigest.getInstance(type);
      messageDigest.update(hashString.getBytes());
      byte[] mdbytes = messageDigest.digest();
      for (byte hashByte : mdbytes) {
        hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return hash.toString();
  }
}
