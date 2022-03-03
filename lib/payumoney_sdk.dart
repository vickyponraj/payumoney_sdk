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



  Future<Map<dynamic,dynamic>?> buildPaymentParams({
     required String amount,
     required String transactionId,
     required String phone,
     required String productInfo,
     required String firstName,
     required String email,
     required String hash,
     required String salt,
     String successURL="https://www.payumoney.com/mobileapp/payumoney/success.php",
     String failureURL="https://www.payumoney.com/mobileapp/payumoney/failure.php",
     required String merchantKey,
     bool isProduction=false,
     String? userCredentials,
     String merchantName="Payu",
    bool showExitConfirmation=true,
    String paymentHash="",
   })async{
     ///Success URL="https://www.payumoney.com/mobileapp/payumoney/success.php"
     ///Falure URL="https://www.payumoney.com/mobileapp/payumoney/failure.php"

   try{
   final data = await _channel.invokeMethod("buildPaymentParams", {
        "amount": amount,
        "transactionId": transactionId,
        "phone": phone,
        "productInfo": productInfo,
        "firstName": firstName,
        "email": email,
        "successURL": successURL,
        "failureURL": failureURL,
        "isProduction": isProduction,
        "merchantKey": merchantKey,
        "userCredentials": userCredentials,
        "hash": hash,
        "salt": salt,
        "merchantName": merchantName,
        "showExitConfirmation": showExitConfirmation,
        "_payment_hash_value": paymentHash
      });

      return data;
    } catch (e) {
      debugPrint(e.toString());

      final errorResponse = {"status": "failed", "message": "payment canceled"};
      return errorResponse;
    }
  }

  Future<Map<dynamic, dynamic>?> validateCardNumber(String cardNumber) async {
    try {
      final data = await _channel.invokeMethod("validateCardNumber", {"cardNumber": cardNumber});
      return data;
    } catch (e) {
      final errorResponse = {"status": "failed", "message": "SDK Error"};
      return errorResponse;
    }
  }

  Future<Map<dynamic, dynamic>?> getPgModes() async {
    try {
      final data = await _channel.invokeMethod("getPgModes");
      return data;
    } catch (e) {
      final errorResponse = {"status": "failed","error":"${e.toString()}",};
      return errorResponse;
    }
  }

  Future<Map<dynamic, dynamic>?> buildAndPayUsingCoreSDK({
    required String amount,
    required String transactionId,
    required String phone,
    required String productInfo,
    required String firstName,
    required String email,
    required String hash,
    required String salt,
    String successURL =
        "https://www.payumoney.com/mobileapp/payumoney/success.php",
    String failureURL =
        "https://www.payumoney.com/mobileapp/payumoney/failure.php",
    required String merchantKey,
    bool isProduction = false,
    String? userCredentials,
    String merchantName = "Payu",
    bool showExitConfirmation = true,
    String paymentHash = "",
    String accountNumber = "",
    String bankCode = "",
    String mobileSDKHash = "",
    String vasSDKHash = "",
    String ifscCode = "",
  }) async {
    ///Success URL="https://www.payumoney.com/mobileapp/payumoney/success.php"
    ///Falure URL="https://www.payumoney.com/mobileapp/payumoney/failure.php"

    try {
      final data = await _channel.invokeMethod("payUsingNetBanking", {
        "amount": amount,
        "transactionId": transactionId,
        "phone": phone,
        "productInfo": productInfo,
        "firstName": firstName,
        "email": email,
        "successURL": successURL,
        "failureURL": failureURL,
        "isProduction": isProduction ? 0 : 0,
        "merchantKey": merchantKey,
        "userCredentials": userCredentials,
        "hash": hash,
        "salt": salt,
        "merchantName": merchantName,
        "showExitConfirmation": showExitConfirmation,
        "paymentHash": paymentHash,
        "vasSDKHash": vasSDKHash,
        "mobileSDKHash": mobileSDKHash,
        "bankCode": bankCode,
        "ifscCode": ifscCode,
        "accountNumber": accountNumber
      });

      return data;
    } catch (e) {
      debugPrint(e.toString());

      final errorResponse = {"status": "failed", "message": "payment canceled"};
      return errorResponse;
    }
  }

  Future<void> setPayuSiParam({
    required bool isFreeTrial,
    required String billingAmt,
    required String billingCycle,
    required String setBillingInterval,
    required String paymentStartDate,
    required String paymentEndDate,
    required String remarks,
  }) async {
    try {
      _channel.invokeMethod("setPayuSiParam", {
        "isFreeTrial": "${isFreeTrial}",
        "billingAmt": billingAmt,
        "billingCycle": billingCycle,
        "setBillingInterval": setBillingInterval,
        "paymentStartDate": paymentStartDate,
        "paymentEndDate": paymentEndDate,
        "remarks": remarks
      });
    }catch(e){
      debugPrint(e.toString());
    }
 }



}
