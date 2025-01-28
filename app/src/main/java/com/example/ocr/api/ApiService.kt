package com.example.ocr.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


data class Place(
    @SerializedName("place name") val placeName: String,
    @SerializedName("longitude") val longitude: String,
    @SerializedName("state") val state: String,
    @SerializedName("state abbreviation") val stateAbbreviation: String,
    @SerializedName("latitude") val latitude: String
)

data class ApiResponse(
    @SerializedName("post code") val postCode: String,
    @SerializedName("country") val country: String,
    @SerializedName("country abbreviation") val countryAbbreviation: String,
    @SerializedName("places") val places: List<Place>
)

interface ApiService {
    @GET("{country}/{zipcode}")
    suspend fun getZipCodeInfo(
        @Path("country") country: String,
        @Path("zipcode") zipCode: String
    ): ApiResponse
}
