// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payu_response_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PayuResponseModel _$PayuResponseModelFromJson(Map<String, dynamic> json) {
  return PayuResponseModel(
    status: json['status'] as bool,
    message: json['message'] as String,
    data: json['data'] == null
        ? null
        : PayResponseData.fromJson(json['data'] as Map<String, dynamic>),
  )..errorMsg = json['errorMsg'] as String;
}

Map<String, dynamic> _$PayuResponseModelToJson(PayuResponseModel instance) =>
    <String, dynamic>{
      'status': instance.status,
      'message': instance.message,
      'data': instance.data,
      'errorMsg': instance.errorMsg,
    };

PayResponseData _$PayResponseDataFromJson(Map<String, dynamic> json) {
  return PayResponseData(
    email: json['email'] as String,
    hash: json['hash'] as String,
    mobile: json['mobile'] as String,
    name: json['name'] as String,
    productName: json['product_name'] as String,
    orderId: json['txn'] as String,
  );
}

Map<String, dynamic> _$PayResponseDataToJson(PayResponseData instance) =>
    <String, dynamic>{
      'email': instance.email,
      'hash': instance.hash,
      'mobile': instance.mobile,
      'name': instance.name,
      'product_name': instance.productName,
      'txn': instance.orderId,
    };
