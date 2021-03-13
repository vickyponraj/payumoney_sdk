import 'package:json_annotation/json_annotation.dart';

part 'payu_response_model.g.dart';


@JsonSerializable()
class PayuResponseModel{
  bool status;
  String message;

  @JsonKey(name:"data")
  PayResponseData data;

  String errorMsg;

  PayuResponseModel.withError({String err}):
      this.errorMsg=err;


  PayuResponseModel({this.status,this.message,this.data});

  factory PayuResponseModel.fromJson(Map<String,dynamic> json)=>
      _$PayuResponseModelFromJson(json);


}



@JsonSerializable()
class PayResponseData{
  String email;
  String hash;
  String mobile;
  String name;

  @JsonKey(name:"product_name")
  String productName;

  @JsonKey(name:"txn")
  String orderId;


  PayResponseData({this.email,this.hash,this.mobile,this.name,this.productName,this.orderId});

  factory PayResponseData.fromJson(Map<String,dynamic> json)=>_$PayResponseDataFromJson(json);
}