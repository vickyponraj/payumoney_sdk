import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class PayumoneySdk {
  static final PayumoneySdk _instance = PayumoneySdk._internal();

  factory PayumoneySdk() {
    return _instance;
  }

  PayumoneySdk._internal();

  static const MethodChannel _channel = const MethodChannel('payumoney_sdk');




   Future<dynamic> buildPaymentParams({
     @required String amount,
     @required String transactionId,
     @required String phone,
     @required String productInfo,
     @required String firstName,
     @required String email,
     @required String hash,
     String successURL="https://www.payumoney.com/mobileapp/payumoney/success.php",
     String failureURL="https://www.payumoney.com/mobileapp/payumoney/failure.php",
     @required String merchantKey,
     bool isProduction=false,
     String userCredentials
   })async{
     ///Success URL="https://www.payumoney.com/mobileapp/payumoney/success.php"
     ///Falure URL="https://www.payumoney.com/mobileapp/payumoney/failure.php"

   try{
     await _channel.invokeMethod("buildPaymentParams",{
       "amount":amount,
       "transactionId":transactionId,
       "phone":phone,
       "productInfo":productInfo,
       "firstName":firstName,
       "email":email,
       "successURL":successURL,
       "failureURL":failureURL,
       "isProduction":isProduction,
       "merchantKey":merchantKey,
       "userCredentials":userCredentials,
       "hash":hash
     });
   }catch (e){
     debugPrint(e.toString());
   }
   }


 Future<void> setPayuSiParam(
     @required bool isFreeTrial,
     @required String billingAmt,
     @required String billingCycle,
     @required String setBillingInterval,
     @required String paymentStartDate,
     @required String paymentEndDate,
     @required String remarks,

     ){
    try{
      _channel.invokeMethod("setPayuSiParam",{
        "isFreeTrial":"${isFreeTrial}",
        "billingAmt":billingAmt,
        "billingCycle":billingCycle,
        "setBillingInterval":setBillingInterval,
        "paymentStartDate":paymentStartDate,
        "paymentEndDate":paymentEndDate,
        "remarks":remarks

      });
    }catch(e){
      debugPrint(e.toString());
    }
 }



}
