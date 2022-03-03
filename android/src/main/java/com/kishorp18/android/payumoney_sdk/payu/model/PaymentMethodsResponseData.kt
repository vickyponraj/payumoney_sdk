package com.kishorp18.android.payumoney_sdk.payu.model


data class PaymentMethodsResponseData (
		val cashCard: List<PgModeStatusData>?,
		val creditCard: List<PgModeStatusData>?,
		val debitCard: List<PgModeStatusData>?,
		val emi: List<EMI>?,
		val genericIntent: GenericIntent?,
		val lazyPay: List<PgModeStatusData>?,
		val netBanks: List<PgModeStatusData>?,
		val phonePE: PgModeStatusData?,
		val responseStatus: ResponseStatus?,
		val siBankList: List<PgModeStatusData>?,
		val standingInstructions: List<PgModeStatusData>?,
		val twid: PgModeStatusData?,
		val upi: GenericIntent?
)


data class PgModeStatusData (
		val bankCode: String,
		val bankID: String,
		val bankName: String,
		val isBankDown: Boolean,
		val pgID: String
)



data class EMI (
		val bankCode: String,
		val bankName: String,
		val bankTitle: String,
		val isBankDown: Boolean,
		val minAmount: String,
		val pgID: String
)


data class GenericIntent (
		val bankID: String,
		val isBankDown: Boolean,
		val pgID: String,
		val showForm: String,
		val title: String
)


data class ResponseStatus (
		val a: String,
		val b: String,
		val c: Long
)
