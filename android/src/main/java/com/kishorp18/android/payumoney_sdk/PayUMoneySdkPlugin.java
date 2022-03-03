package com.kishorp18.android.payumoney_sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUBillingCycle;
import com.payu.base.models.PayUPaymentParams;
import com.payu.base.models.PayUSIParams;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.models.PayUCheckoutProConfig;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;


import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Model.MerchantWebService;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.Payu.PayuUtils;
import com.payu.india.PostParams.MerchantWebServicePostParams;
import com.payu.india.Tasks.GetPaymentRelatedDetailsTask;
import com.payu.paymentparamhelper.PaymentParams;
import com.payu.paymentparamhelper.PaymentPostParams;
import com.payu.paymentparamhelper.PostData;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.Log;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import io.flutter.plugin.common.PluginRegistry;


/** PayumoneySdkPlugin */
public class PayUMoneySdkPlugin implements FlutterPlugin, MethodCallHandler,PluginRegistry.ActivityResultListener,ActivityAware {
  private MethodChannel channel;
  private MethodChannel.Result mainResult;
  private Activity activity;
  private  Context context;
  private ActivityPluginBinding pluginBinding;
  private static final String TAG = "PayuMoney";
  private boolean isProduction = false;
  PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
  PayUPaymentParams payUPaymentParams =null;
  PayUSIParams siDetails=null;
  PayUChecksum checksum = null;
  PayuConfig payuConfig = null;
  final private PayuUtils payuUtils = new PayuUtils();





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
    }else if(call.method.equals("payUsingNetBanking")){
         payUsingNetBanking(call);
    }else if(call.method.equals("payUsingGenericUPI")){
         payUsingGenericUPI(call);
    }else if(call.method.equals("validateCardNumber")){
         HashMap<String, Object> validateCardNumber = validateCardNumber(call);
         result.success(validateCardNumber);
    }else if(call.method.equals("getPgModes")){
         getPgExtraData(call,result);
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

    if(requestCode == PayuConstants.PAYU_REQUEST_CODE){
        if(data != null){
            String payuResult = data.getStringExtra("result");
            String payuResponse = data.getStringExtra("payu_response");

            android.util.Log.d(TAG, "payuResult: "+payuResult);
            android.util.Log.d(TAG, "payuResponse: "+payuResponse);

            HashMap<String,Object> result = new HashMap<>();
            result.put("status", "failed");
            result.put("message","Payment canceled");
            mainResult.success(result);
        }
    }

    return  false;
  }

  private void getPgExtraData(MethodCall call, Result result){

      PostData postData = new PostData();
      String data = "";


      if ((postData = calculateHash("3yaFx2", PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, "GODesgUc8L3kAjrTrrzBaQBwBc4XPB0U")) != null && postData.getCode() == PayuErrors.NO_ERROR)
          data  = postData.getResult();

      MerchantWebService merchantWebService = new MerchantWebService();
      merchantWebService.setKey("3yaFx2");
      merchantWebService.setCommand(PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK);
      merchantWebService.setVar1(PayuConstants.DEFAULT);
      merchantWebService.setHash(data);


      payuConfig = new PayuConfig();
      payuConfig.setEnvironment(0);

      PostData paymentPostData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();

      if (paymentPostData.getCode() == PayuErrors.NO_ERROR) {
          payuConfig.setData("857772bb2089b4f9b3c3b5c05eabfc8316155e18f67ca05110f8c8848f7cc10d84b37e450689354ddb75a55b5a80f35d1c068bfaa66006f467a12a708f166c6e");

          GetPaymentRelatedDetailsTask paymentRelatedDetailsForMobileSdkTask = new GetPaymentRelatedDetailsTask(payuResponse -> {

              HashMap<String,Object> validatedResult = new HashMap<>();
              validatedResult.put("isDebitCardAvailable",payuResponse.isDebitCardAvailable());
              validatedResult.put("isNetBanksAvailable",payuResponse.isNetBanksAvailable());
              validatedResult.put("isUpiAvailable",payuResponse.isUpiAvailable());
              validatedResult.put("isGenericIntentAvailable",payuResponse.isGenericIntentAvailable());
              validatedResult.put("isPhonePeIntentAvailable",payuResponse.isPhonePeIntentAvailable());
              validatedResult.put("netBankingList",payuResponse.getNetBanks());


              result.success(validatedResult);

          });

          paymentRelatedDetailsForMobileSdkTask.execute(payuConfig);


      }else{
          result.error("2001","PaymentPostData Failed",paymentPostData.getStatus());
      }

  }



  public HashMap<String,Object> validateCardNumber(MethodCall call){
      HashMap<String,Object> validatedResult = new HashMap<>();
      validatedResult.put("isValid",payuUtils.validateCardNumber((String)call.argument(Constants.PayU_CardNumber)));
      validatedResult.put("cardNumber",(String)call.argument(Constants.PayU_CardNumber));
      validatedResult.put("issuer",payuUtils.getIssuer((String)call.argument(Constants.PayU_CardNumber)));
      return  validatedResult;
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

  private void payUsingNewMID_NB(MethodCall call){



  }


  private void buildPaymentParams(MethodCall call) {
      PayUCheckoutProConfig payUCheckoutProConfig = new PayUCheckoutProConfig ();
      payUCheckoutProConfig.setMerchantName((String)call.argument("merchantName"));
      String paymentHashCode = (String) call.argument(Constants.PaymentHashValue);

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

      startPayment(this.payUPaymentParams,payUCheckoutProConfig,(String)call.argument("salt"),paymentHashCode);



    } catch (Exception e) {
      mainResult.error("ERROR", e.getMessage(), null);
      Log.d(TAG, "Error : " + e.toString());
    }
  }


  private void startPayment(PayUPaymentParams payUPaymentParams,PayUCheckoutProConfig payUCheckoutProConfig,final  String salt,String paymentHashCode){
   PayUCheckoutPro.open(
            activity,
            payUPaymentParams,
            payUCheckoutProConfig,
            new PayUCheckoutProListener() {

              @Override
              public void onPaymentSuccess(Object response) {
                //Cast response object to HashMap
                HashMap<String,Object> result = (HashMap<String, Object>) response;
                String payuResponse = (String)result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                result.put("status", "success");
                result.put("message","Payment success");
                mainResult.success(result);

              }

              @Override
              public void onPaymentFailure(Object response) {
                //Cast response object to HashMap
                HashMap<String,Object> result = (HashMap<String, Object>) response;
                String payuResponse = (String)result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                  android.util.Log.d(TAG, "onPaymentFailure: "+payuResponse);
                  android.util.Log.d(TAG, "onPaymentFailure: "+merchantResponse);
                result.put("status", "failed");
                result.put("message","Payment failed");
                mainResult.success(result);
              }

              @Override
              public void onPaymentCancel(boolean isTxnInitiated) {

                  HashMap<String,Object> result =new HashMap<String, Object>();
                result.put("status", "failed");
                result.put("message","Payment canceled");
                mainResult.success(result);
              }

              @Override
              public void onError(ErrorResponse errorResponse) {
                String errorMessage = errorResponse.getErrorMessage();
                HashMap<String,Object> result =new HashMap<String, Object>();
                  android.util.Log.d(TAG, "onPaymentError: "+errorMessage);
                  result.put("status", "failed");
                result.put("message","Something went wrong");
                mainResult.success(result);
              }

              @Override
              public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                //For setting webview properties, if any. Check Customized Integration section for more details on this
              }

              @Override
              public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {

                  HashMap<String, String> dataMap = new HashMap<>();
                  String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);


                  if (!paymentHashCode.isEmpty()){
                      dataMap.put(hashName, paymentHashCode);
                  }else{
                      String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                      String hashVal="";
                      if(hashName!=null&&(!hashName.isEmpty())&&hashName.equals("vas_for_mobile_sdk")){
                          hashVal=hashData+salt;
                      }else {
                          hashVal=hashData+salt;
                      }
                      String calculatedHash=hashCal("SHA-512", hashVal);
                      dataMap.put(hashName, calculatedHash);
                  }
                  hashGenerationListener.onHashGenerated(dataMap);

              }
            }
    );
  }


    // deprecated, should be used only for testing.
    private PostData calculateHash(String key, String command, String var1, String salt) {
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setKey(key);
        checksum.setCommand(command);
        checksum.setVar1(var1);
        checksum.setSalt(salt);
        return checksum.getHash();
    }


    public PayuHashes generateHashFromSDK(PaymentParams mPaymentParams, String salt, String paymentHash) {
        PayuHashes payuHashes = new PayuHashes();
        PostData postData = new PostData();

//        if(mPaymentParams.getBeneficiaryAccountNumber()== null){

        // payment Hash;
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setPaymentHash(postData.getResult());
        }
        String var1 = mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials();

        String key = mPaymentParams.getKey();


        if ((postData = calculateHash(key, PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // Assign post data first then check for success
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        //vas
        if ((postData = calculateHash(key, PayuConstants.VAS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setVasForMobileSdkHash(postData.getResult());

        String beneficiaryDetailTPV  = "{'beneficiaryAccountNumber':'50100200350998','ifscCode':'HDFC0001867'}";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mPaymentParams.getKey());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getTxnId());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getAmount());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getProductInfo());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getFirstName());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getEmail());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf1());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf2());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf3());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf4());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf5());//||||||
        stringBuilder.append("|");
        stringBuilder.append("|");
        stringBuilder.append("|");
        stringBuilder.append("|");
        stringBuilder.append("|");
        /*
        stringBuilder.append("|");
        stringBuilder.append(beneficiaryDetailTPV);
        */
        stringBuilder.append("|");
        stringBuilder.append(salt);
        String localPaymentHash = hashCal("SHA-512",stringBuilder.toString());

/*

// For single ifsc code
beneficiarydetail = "{'beneficiaryAccountNumber':'917732227242','ifscCode':'SBIN000700'}"

// For multiple ifsc number
beneficiarydetail = "{'beneficiaryAccountNumber':'917732227242|72522762|283228235','ifscCode':'SBIN000700|KTKN2937492|ICIC0002522'}"

// Hash calculation
Hash = sha512(key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||beneficiarydetail|SALT)


        if (mPaymentParams.getSubventionAmount() != null && !mPaymentParams.getSubventionAmount().isEmpty()){
            subventionHash = calculateHash(""+mPaymentParams.getKey()+"|"+mPaymentParams.getTxnId()+"|"+mPaymentParams.getAmount()+"|"+mPaymentParams.getProductInfo()+"|"+mPaymentParams.getFirstName()+"|"+mPaymentParams.getEmail()+"|"+mPaymentParams.getUdf1()+"|"+mPaymentParams.getUdf2()+"|"+mPaymentParams.getUdf3()+"|"+mPaymentParams.getUdf4()+"|"+mPaymentParams.getUdf5()+"||||||"+salt+"|"+mPaymentParams.getSubventionAmount());
        }
        if (mPaymentParams.getSiParams()!=null){
            siHash = calculateHash(""+mPaymentParams.getKey()+"|"+mPaymentParams.getTxnId()+"|"+mPaymentParams.getAmount()+"|"+mPaymentParams.getProductInfo()+"|"+mPaymentParams.getFirstName()+"|"+mPaymentParams.getEmail()+"|"+mPaymentParams.getUdf1()+"|"+mPaymentParams.getUdf2()+"|"+mPaymentParams.getUdf3()+"|"+mPaymentParams.getUdf4()+"|"+mPaymentParams.getUdf5()+"||||||"+prepareSiDetails()+"|"+salt);
        }

        *//*}

        else {
            String hashString = merchantKey + "|" + mPaymentParams.getTxnId() + "|" + mPaymentParams.getAmount() + "|" + mPaymentParams.getProductInfo() + "|" + mPaymentParams.getFirstName() + "|" + mPaymentParams.getEmail() + "|" + mPaymentParams.getUdf1() + "|" + mPaymentParams.getUdf2() + "|" + mPaymentParams.getUdf3() + "|" + mPaymentParams.getUdf4() + "|" + mPaymentParams.getUdf5() + "||||||{\"beneficiaryAccountNumber\":\"" +mPaymentParams.getBeneficiaryAccountNumber()+ "\"}|" + salt;

            paymentHash1 = calculateHash(hashString);
            payuHashes.setPaymentHash(paymentHash1);



        }*//*

        // checksum for payemnt related details
        // var1 should be either user credentials or default
        String var1 = mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials();
        String key = mPaymentParams.getKey();

        if ((postData = calculateHash(key, PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // Assign post data first then check for success
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        //vas
        if ((postData = calculateHash(key, PayuConstants.VAS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setVasForMobileSdkHash(postData.getResult());

        // getIbibocodes
        if ((postData = calculateHash(key, PayuConstants.GET_MERCHANT_IBIBO_CODES, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setMerchantIbiboCodesHash(postData.getResult());

        if (!var1.contentEquals(PayuConstants.DEFAULT)) {
            // get user card
            if ((postData = calculateHash(key, PayuConstants.GET_USER_CARDS, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // todo rename storedc ard
                payuHashes.setStoredCardsHash(postData.getResult());
            // save user card
            if ((postData = calculateHash(key, PayuConstants.SAVE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setSaveCardHash(postData.getResult());
            // delete user card
            if ((postData = calculateHash(key, PayuConstants.DELETE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setDeleteCardHash(postData.getResult());
            // edit user card
            if ((postData = calculateHash(key, PayuConstants.EDIT_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setEditCardHash(postData.getResult());
        }

        if (mPaymentParams.getOfferKey() != null) {
            postData = calculateHash(key, PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey(), salt);
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                payuHashes.setCheckOfferStatusHash(postData.getResult());
            }
        }

        if (mPaymentParams.getOfferKey() != null && (postData = calculateHash(key, PayuConstants.CHECK_OFFER_STATUS, mPaymentParams.getOfferKey(), salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setCheckOfferStatusHash(postData.getResult());
        }

        // we have generated all the hases now lest launch sdk's ui
        */
//        launchSdkUI(payuHashes);
        android.util.Log.d(TAG, "payUsingCoreSDK:SDK: "+payuHashes.getPaymentHash());
        android.util.Log.d(TAG, "Local Hash Value   : "+stringBuilder.toString());
        android.util.Log.d(TAG, "payUsing Local :SDK: "+localPaymentHash);
        android.util.Log.d(TAG, "payUsing Server:SDK: "+paymentHash);

        mPaymentParams.setHash(localPaymentHash);
        PostData paymentPostParams = new PaymentPostParams(mPaymentParams, PayuConstants.NB).getPaymentPostParams();

        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuConfig.setData(paymentPostParams.getResult());

            Intent intent = new Intent(PayUMoneySdkPlugin.this.activity, PaymentsActivity.class);
            intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
            PayUMoneySdkPlugin.this.activity.startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        }


        return payuHashes;
    }
    public PayuHashes generateHashFromSDKNewMID(PaymentParams mPaymentParams, String salt, String paymentHash) {
        PayuHashes payuHashes = new PayuHashes();
        PostData postData = new PostData();

//        if(mPaymentParams.getBeneficiaryAccountNumber()== null){

        // payment Hash;
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setPaymentHash(postData.getResult());
        }
        String var1 = mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials();

        String key = mPaymentParams.getKey();


        if ((postData = calculateHash(key, PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // Assign post data first then check for success
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        //vas
        if ((postData = calculateHash(key, PayuConstants.VAS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setVasForMobileSdkHash(postData.getResult());

        String beneficiaryDetailTPV  = "{'beneficiaryAccountNumber':'50100200350998','ifscCode':'HDFC0001867'}";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mPaymentParams.getKey());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getTxnId());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getAmount());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getProductInfo());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getFirstName());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getEmail());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf1());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf2());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf3());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf4());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf5());//||||||
        stringBuilder.append("|");
        stringBuilder.append("|");
        stringBuilder.append("|");
        stringBuilder.append("|");
        stringBuilder.append("|");
        stringBuilder.append("|");
        stringBuilder.append(beneficiaryDetailTPV);
        stringBuilder.append("|");
        stringBuilder.append(salt);
        String localPaymentHash = hashCal("SHA-512",stringBuilder.toString());
//        sha512(key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||beneficiarydetail|SALT)
/*

// For single ifsc code
beneficiarydetail = "{'beneficiaryAccountNumber':'917732227242','ifscCode':'SBIN000700'}"

// For multiple ifsc number
beneficiarydetail = "{'beneficiaryAccountNumber':'917732227242|72522762|283228235','ifscCode':'SBIN000700|KTKN2937492|ICIC0002522'}"

// Hash calculation
Hash = sha512(key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||beneficiarydetail|SALT)


        if (mPaymentParams.getSubventionAmount() != null && !mPaymentParams.getSubventionAmount().isEmpty()){
            subventionHash = calculateHash(""+mPaymentParams.getKey()+"|"+mPaymentParams.getTxnId()+"|"+mPaymentParams.getAmount()+"|"+mPaymentParams.getProductInfo()+"|"+mPaymentParams.getFirstName()+"|"+mPaymentParams.getEmail()+"|"+mPaymentParams.getUdf1()+"|"+mPaymentParams.getUdf2()+"|"+mPaymentParams.getUdf3()+"|"+mPaymentParams.getUdf4()+"|"+mPaymentParams.getUdf5()+"||||||"+salt+"|"+mPaymentParams.getSubventionAmount());
        }
        if (mPaymentParams.getSiParams()!=null){
            siHash = calculateHash(""+mPaymentParams.getKey()+"|"+mPaymentParams.getTxnId()+"|"+mPaymentParams.getAmount()+"|"+mPaymentParams.getProductInfo()+"|"+mPaymentParams.getFirstName()+"|"+mPaymentParams.getEmail()+"|"+mPaymentParams.getUdf1()+"|"+mPaymentParams.getUdf2()+"|"+mPaymentParams.getUdf3()+"|"+mPaymentParams.getUdf4()+"|"+mPaymentParams.getUdf5()+"||||||"+prepareSiDetails()+"|"+salt);
        }

        *//*}

        else {
            String hashString = merchantKey + "|" + mPaymentParams.getTxnId() + "|" + mPaymentParams.getAmount() + "|" + mPaymentParams.getProductInfo() + "|" + mPaymentParams.getFirstName() + "|" + mPaymentParams.getEmail() + "|" + mPaymentParams.getUdf1() + "|" + mPaymentParams.getUdf2() + "|" + mPaymentParams.getUdf3() + "|" + mPaymentParams.getUdf4() + "|" + mPaymentParams.getUdf5() + "||||||{\"beneficiaryAccountNumber\":\"" +mPaymentParams.getBeneficiaryAccountNumber()+ "\"}|" + salt;

            paymentHash1 = calculateHash(hashString);
            payuHashes.setPaymentHash(paymentHash1);



        }*//*

        // checksum for payemnt related details
        // var1 should be either user credentials or default
        String var1 = mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials();
        String key = mPaymentParams.getKey();

        if ((postData = calculateHash(key, PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // Assign post data first then check for success
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        //vas
        if ((postData = calculateHash(key, PayuConstants.VAS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setVasForMobileSdkHash(postData.getResult());

        // getIbibocodes
        if ((postData = calculateHash(key, PayuConstants.GET_MERCHANT_IBIBO_CODES, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setMerchantIbiboCodesHash(postData.getResult());

        if (!var1.contentEquals(PayuConstants.DEFAULT)) {
            // get user card
            if ((postData = calculateHash(key, PayuConstants.GET_USER_CARDS, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // todo rename storedc ard
                payuHashes.setStoredCardsHash(postData.getResult());
            // save user card
            if ((postData = calculateHash(key, PayuConstants.SAVE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setSaveCardHash(postData.getResult());
            // delete user card
            if ((postData = calculateHash(key, PayuConstants.DELETE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setDeleteCardHash(postData.getResult());
            // edit user card
            if ((postData = calculateHash(key, PayuConstants.EDIT_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setEditCardHash(postData.getResult());
        }

        if (mPaymentParams.getOfferKey() != null) {
            postData = calculateHash(key, PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey(), salt);
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                payuHashes.setCheckOfferStatusHash(postData.getResult());
            }
        }

        if (mPaymentParams.getOfferKey() != null && (postData = calculateHash(key, PayuConstants.CHECK_OFFER_STATUS, mPaymentParams.getOfferKey(), salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setCheckOfferStatusHash(postData.getResult());
        }

        // we have generated all the hases now lest launch sdk's ui
        */
//        launchSdkUI(payuHashes);
        android.util.Log.d(TAG, "payUsingCoreSDK:SDK: "+payuHashes.getPaymentHash());
        android.util.Log.d(TAG, "Local Hash Value   : "+stringBuilder.toString());
        android.util.Log.d(TAG, "payUsing Local :SDK: "+localPaymentHash);
        android.util.Log.d(TAG, "payUsing Server:SDK: "+paymentHash);

        mPaymentParams.setHash(payuHashes.getPaymentHash());
        PostData paymentPostParams = new PaymentPostParams(mPaymentParams, PayuConstants.NB).getPaymentPostParams();

        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuConfig.setData(paymentPostParams.getResult());

            Intent intent = new Intent(PayUMoneySdkPlugin.this.activity, PaymentsActivity.class);
            intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
            PayUMoneySdkPlugin.this.activity.startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        }


        return payuHashes;
    }

    public void payUsingGenericUPI(MethodCall call){


        PaymentParams mPaymentParams = new PaymentParams();

        payuConfig = new PayuConfig();
        payuConfig.setEnvironment(0);

        mPaymentParams.setKey("3TnMpV");
        mPaymentParams.setAmount("1");
        mPaymentParams.setProductInfo("Test-UPI-Payment");
        mPaymentParams.setFirstName("Vignesh");
        mPaymentParams.setTxnId("TestUPI"+System.currentTimeMillis());
        mPaymentParams.setPhone("8248611901");
        mPaymentParams.setEmail("vignesh.p@siply.in");
        mPaymentParams.setSurl("https://aws-east.siply.in/siplyadminpayments/api/v1/admin/payments/callback?status=success");
        mPaymentParams.setFurl("https://aws-east.siply.in/siplyadminpayments/api/v1/admin/payments/callback?status=failed");
        mPaymentParams.setNotifyURL(mPaymentParams.getSurl());  //for lazy pay
//      mPaymentParams.env3((String)call.argument(Constants.PayU_IsProduction));
//        mPaymentParams.setUserCredentials("");
//        mPaymentParams.setBeneficiaryAccountNumber("50100200350998");
//        mPaymentParams.setIfscCode("HDFC0001867");
        mPaymentParams.setBankCode("HDFNBTPV");
        mPaymentParams.setUserCredentials("Zhgh53 : vignesh.p@siply.in");
//        mPaymentParams.setBankCode((String)call.argument(Constants.PayU_BankCode));
        mPaymentParams.setUdf1("udf1");
        mPaymentParams.setUdf2("udf2");
        mPaymentParams.setUdf3("udf3");
        mPaymentParams.setUdf4("udf4");
        mPaymentParams.setUdf5("udf5");

        PayuHashes payuHashes = generateHashFromSDKNewMID(mPaymentParams, "g0nGFe03",(String)call.argument(Constants.PayU_PaymentHash));


    }

    public void payUsingNetBanking(MethodCall call){


      PaymentParams mNetBankingPaymentParams = new PaymentParams();

      payuConfig = new PayuConfig();
        android.util.Log.d(TAG, "payUsingNetBanking: Test");
        android.util.Log.d(TAG, "payUsingNetBanking: "+call.argument(Constants.PayU_IsProduction));
        android.util.Log.d(TAG, "Test: "+call.argument(Constants.PayU_IsProduction));
      payuConfig.setEnvironment((int) call.argument(Constants.PayU_IsProduction));
      mNetBankingPaymentParams.setKey((String)call.argument(Constants.PayU_MerchantKey));
      mNetBankingPaymentParams.setAmount((String)call.argument(Constants.PayU_Amount));
      mNetBankingPaymentParams.setProductInfo((String)call.argument(Constants.PayU_ProductInfo));
      mNetBankingPaymentParams.setFirstName((String)call.argument(Constants.PayU_FirstName));
      mNetBankingPaymentParams.setTxnId((String)call.argument(Constants.PayU_TransactionId));
      mNetBankingPaymentParams.setPhone((String)call.argument(Constants.PayU_PhoneNumber));
      mNetBankingPaymentParams.setEmail((String)call.argument(Constants.PayU_Email));
      mNetBankingPaymentParams.setSurl((String)call.argument(Constants.PayU_SuccessURL));
      mNetBankingPaymentParams.setFurl((String)call.argument(Constants.PayU_FailureURL));
//      mNetBankingPaymentParams.env3((String)call.argument(Constants.PayU_IsProduction));
      mNetBankingPaymentParams.setUserCredentials((String)call.argument(Constants.PayU_UserCredentials));
      mNetBankingPaymentParams.setBeneficiaryAccountNumber((String)call.argument(Constants.PayU_AccountNumber));
//      mNetBankingPaymentParams.setIfscCode((String)call.argument(Constants.PayU_IfscCode));
      mNetBankingPaymentParams.setBankCode((String)call.argument(Constants.PayU_BankCode));
      mNetBankingPaymentParams.setUdf1("udf1");
      mNetBankingPaymentParams.setUdf2("udf2");
      mNetBankingPaymentParams.setUdf3("udf3");
      mNetBankingPaymentParams.setUdf4("udf4");
      mNetBankingPaymentParams.setUdf5("udf5");
      android.util.Log.d(TAG, "payUsingCoreSDK: "+(String)call.argument(Constants.PayU_PaymentHash));
      android.util.Log.d(TAG, "Bank Code: "+(String)call.argument(Constants.PayU_BankCode));
      mNetBankingPaymentParams.setHash(call.argument(Constants.PayU_PaymentHash));

        //        PayuHashes payuHashes = generateHashFromSDK(mNetBankingPaymentParams, "aGXzo7lo",(String)call.argument(Constants.PayU_PaymentHash));

        doNetBankingPayment(mNetBankingPaymentParams,"GODesgUc8L3kAjrTrrzBaQBwBc4XPB0U",call.argument(Constants.PayU_PaymentHash));

       /*
        PostData postData = null;
        try {
            postData = new PaymentPostParams(mNetBankingPaymentParams, PayuConstants.NB).getPaymentPostParams();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuConfig.setData(postData.getResult());
            Intent intent = new Intent(PayUMoneySdkPlugin.this.activity, PaymentsActivity.class);
            intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
            PayUMoneySdkPlugin.this.activity.startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        } else {
            Log.d("generateHashFromSDK", "generateHashFromSDK: "+postData.getResult());
        }

*/

  }

    public void doNetBankingPayment(PaymentParams mPaymentParams, String salt, String paymentHash) {
        PayuHashes payuHashes = new PayuHashes();
        PostData postData = new PostData();

//        if(mPaymentParams.getBeneficiaryAccountNumber()== null){

        // payment Hash;
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setPaymentHash(postData.getResult());
        }


        String accKey = "\"beneficiaryAccountNumber\"";
        String beneficiaryDetailTPV  = "{"+accKey+":\""+mPaymentParams.getBeneficiaryAccountNumber()+"\"}";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mPaymentParams.getKey());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getTxnId());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getAmount());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getProductInfo());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getFirstName());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getEmail());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf1());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf2());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf3());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf4());
        stringBuilder.append("|");
        stringBuilder.append(mPaymentParams.getUdf5());//||||||
        stringBuilder.append("|");
        stringBuilder.append("|");
        stringBuilder.append("|");
        stringBuilder.append("|");
        stringBuilder.append("|");
        stringBuilder.append("|");
        stringBuilder.append(beneficiaryDetailTPV);
        stringBuilder.append("|");
        stringBuilder.append(salt);
        String localPaymentHash = hashCal("SHA-512",stringBuilder.toString());
        payuHashes.setPaymentHash(localPaymentHash);

        // checksum for payemnt related details
        // var1 should be either user credentials or default
        String var1 = mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials();
        String key = mPaymentParams.getKey();

        if ((postData = calculateHash(key, PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // Assign post data first then check for success
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        //vas
        if ((postData = calculateHash(key, PayuConstants.VAS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setVasForMobileSdkHash(postData.getResult());

        // getIbibocodes
        if ((postData = calculateHash(key, PayuConstants.GET_MERCHANT_IBIBO_CODES, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setMerchantIbiboCodesHash(postData.getResult());

        if (!var1.contentEquals(PayuConstants.DEFAULT)) {
            // get user card
            if ((postData = calculateHash(key, PayuConstants.GET_USER_CARDS, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // todo rename storedc ard
                payuHashes.setStoredCardsHash(postData.getResult());
            // save user card
            if ((postData = calculateHash(key, PayuConstants.SAVE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setSaveCardHash(postData.getResult());
            // delete user card
            if ((postData = calculateHash(key, PayuConstants.DELETE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setDeleteCardHash(postData.getResult());
            // edit user card
            if ((postData = calculateHash(key, PayuConstants.EDIT_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setEditCardHash(postData.getResult());
        }

        if (mPaymentParams.getOfferKey() != null) {
            postData = calculateHash(key, PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey(), salt);
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                payuHashes.setCheckOfferStatusHash(postData.getResult());
            }
        }

        if (mPaymentParams.getOfferKey() != null && (postData = calculateHash(key, PayuConstants.CHECK_OFFER_STATUS, mPaymentParams.getOfferKey(), salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setCheckOfferStatusHash(postData.getResult());
        }

        android.util.Log.d("HashFromSDK", "generateHashFromSDK: "+stringBuilder.toString());
        android.util.Log.d("HashFromSDK", "generateHashFromSDK: "+localPaymentHash);
        android.util.Log.d("HashFromS2S", "generateHashFromSDK: "+paymentHash);
        // we have generated all the hases now lest launch sdk's ui
        mPaymentParams.setHash(paymentHash);


        postData = null;

        try {
            postData = new PaymentPostParams(mPaymentParams, PayuConstants.NB).getPaymentPostParams();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuConfig.setData(postData.getResult());
            Intent intent = new Intent(PayUMoneySdkPlugin.this.activity, PaymentsActivity.class);
            intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
            PayUMoneySdkPlugin.this.activity.startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        } else {
            Log.d("generateHashFromSDK", "generateHashFromSDK: "+postData.getResult());
        }

    }


    public void payUsingCard(MethodCall call){


        PaymentParams mPaymentParams = new PaymentParams();

        payuConfig = new PayuConfig();
        payuConfig.setEnvironment((int) call.argument(Constants.PayU_IsProduction));


        mPaymentParams.setKey((String)call.argument(Constants.PayU_MerchantKey));
        mPaymentParams.setAmount((String)call.argument(Constants.PayU_Amount));
        mPaymentParams.setProductInfo((String)call.argument(Constants.PayU_ProductInfo));
        mPaymentParams.setFirstName((String)call.argument(Constants.PayU_FirstName));
        mPaymentParams.setTxnId((String)call.argument(Constants.PayU_TransactionId));
        mPaymentParams.setPhone((String)call.argument(Constants.PayU_PhoneNumber));
        mPaymentParams.setEmail((String)call.argument(Constants.PayU_Email));
        mPaymentParams.setSurl((String)call.argument(Constants.PayU_SuccessURL));
        mPaymentParams.setFurl((String)call.argument(Constants.PayU_FailureURL));
//      mPaymentParams.env3((String)call.argument(Constants.PayU_IsProduction));
        mPaymentParams.setUserCredentials((String)call.argument(Constants.PayU_UserCredentials));
        mPaymentParams.setBeneficiaryAccountNumber((String)call.argument(Constants.PayU_AccountNumber));
        mPaymentParams.setIfscCode((String)call.argument(Constants.PayU_IfscCode));
        mPaymentParams.setBankCode((String)call.argument(Constants.PayU_BankCode));
        mPaymentParams.setUdf1(PayuConstants.DEFAULT);
        mPaymentParams.setUdf2(PayuConstants.DEFAULT);
        mPaymentParams.setUdf3(PayuConstants.DEFAULT);
        mPaymentParams.setUdf4(PayuConstants.DEFAULT);
        mPaymentParams.setUdf5(PayuConstants.DEFAULT);

    }




  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    this.activity = binding.getActivity();
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
