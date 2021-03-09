import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:payumoney_sdk/payumoney_sdk.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool response;
  final payu=PayumoneySdk();

  @override
  void initState() {
    super.initState();
    initPayu();
  }


  Future<void> initPayu() async {
    bool ispayuSetup;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
     ispayuSetup=await payu.setupPayuDetails(
         merchantID: "",
         merchantKey: "",
         isProduction: false,
         activityTitle: "Test",
         disableExitConfirmation: true);
    } on PlatformException {
      ispayuSetup=false;
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    if(ispayuSetup){
      payu.startPayment(
          txnid: "IRD",
          amount: "100",
          name:"" ,
          email: "",
          phone: "",
          productName: "",
          hash: "");
    }

    setState(() {
      response =ispayuSetup;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Payumoney SDK'),
        ),
        body: Center(
          child: Text('Payu  setup on: $response\n'),
        ),
      ),
    );
  }
}
