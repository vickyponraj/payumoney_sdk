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

  String _merchantKey;
  String _merchantId;
  bool _isProduction;


  Future<dynamic> startPayment(
      {@required String txnid,
        @required String amount,
        @required String name,
        @required String email,
        @required String phone,
        @required String productName,
        @required String hash}) async {
    try {
      var data = await _channel.invokeMethod("startPayment", {
        "txnid": txnid,
        "hash": hash,
        "amount": amount,
        "phone": phone,
        "email": email,
        "productName": productName,
        "firstname": name
      });
      debugPrint(data);

      return data;
    } catch (e) {
      print(e.toString());
      return null;
    }
  }


  Future<bool> setupPayuDetails(
      {@required String merchantID,
      @required String merchantKey,
      @required bool isProduction,
      @required String activityTitle,
      @required bool disableExitConfirmation}) async {
    final bool response = await _channel.invokeMethod("setupPayuDetails", {
      "merchantID": merchantID,
      "merchantKey": merchantKey,
      "isProduction": isProduction,
      "activityTitle": activityTitle,
      "disableExitConfirmation": disableExitConfirmation,
    });

    if (response) {
      _merchantKey = merchantKey;
      _merchantId = merchantID;
      _isProduction = isProduction;
    }

    return response;
  }



}
