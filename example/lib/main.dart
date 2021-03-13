import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:payumoney_sdk/payumoney_sdk.dart';
import 'package:payumoney_sdk_example/models/payu_response_model.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool isPayuSetup=false;
  final payu=PayumoneySdk();

  @override
  void initState() {
    super.initState();
    initPayu();
  }


  Future<void> initPayu() async {
    bool _ispayuSetup;

    try {
     _ispayuSetup=await payu.setupPayuDetails(
         merchantID: "4955750",
         merchantKey: "k5SC5F",
         isProduction: false,
         activityTitle: "Test",
         disableExitConfirmation: false);
    } on PlatformException {
      _ispayuSetup=false;
    }

    if (!mounted) return;



    setState(() {
      isPayuSetup =_ispayuSetup;
    });
  }



  void startPayment()async{


    final payuOrderId=await getPayuOrderId();

    if(isPayuSetup){
      payu.startPayment(
          txnID: payuOrderId.data.orderId,//Add Txn Id
          amount: "400.00",
          name:payuOrderId.data.name ,
          email: payuOrderId.data.email,
          phone: payuOrderId.data.mobile,
          productName: payuOrderId.data.productName,
          hash: payuOrderId.data.hash);
    }
  }


  Future<PayuResponseModel> getPayuOrderId()async{
    final response= await http.post(
        "https://aws-east.siply.in/siplyadminuser/api/v2/admin/auth/investments/checkoutTransactions?vendor=cqmoneygold",
        headers: {
          "Authorization":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb3VudHJ5X2NvZGUiOiI5MSIsImVtYWlsIjoic2F1cmFiaC5rYW5hdWppYUBvdXRsb29rLmNvbSIsImVtcF9pZCI6IkNRMDExNSIsImVtcHJfaWQiOiJRRFNQTCIsImV4cCI6IjE2MTY4MzYzMTE1NTMiLCJpZCI6MjkzLCJtb2JpbGVOdW1iZXIiOjkwMzI0MDUwODcsIm5hbWUiOiJTYXVyYWJoIEthbmF1amlhIiwic291cmNlIjoid2ViIiwidXNlcl9pZCI6IkF5dW5VX1dHUiIsInVzZXJuYW1lIjo5MDMyNDA1MDg3fQ.o1Ye1jMT4xpRN2lHYEiMh_1LE8xGkZ6KBJGYpxK9z-E"
        },

        body: jsonEncode({
          "gold_list":[
            {
              "vendor_name" : "Augmont Goldtech Private Limited",
              "block_id": "DGoUN6sLV",
              "gold_amount": 400.00,
              "lock_price": 69.41
            }
          ]
        })

    );


    return PayuResponseModel.fromJson(jsonDecode(response.body));
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Payumoney SDK'),
        ),
        body: Center(
          child: Column(
            children: [
              Text('Payu  setup on: $isPayuSetup\n'),
              SizedBox(height: 100,),
              FlatButton(onPressed: (){
                startPayment();
              }, child: Text("Tap me"))
            ],
          ),
        ),
      ),
    );
  }
}
