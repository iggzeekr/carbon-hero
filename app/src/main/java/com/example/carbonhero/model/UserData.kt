package com.example.carbonhero.model

    data class UserData(
        // Transportation Data
        val carMake: String = "",
        val carModel: String = "",
        val vehicleType: String = "",
        val monthlyDrivingDistance: Double = 0.0,
        val transportPreference: String = "",

        // Lifestyle Data
        val dietPreference: String = "",
        val heatingSystem: String = "",
        val houseType: String = "",
        val energyEfficiency: String = "",
        val recycling: Boolean = false,
        val cookingEnergyType: String = "",
        val airTravelFrequency: String = "",

        // Consumption Data
        val monthlyGroceryBill: Double = 0.0,
        val wasteBagSize: String = "",
        val wasteBagCount: Int = 0,
        val tvPcUsageHours: Double = 0.0,
        val internetUsageHours: Double = 0.0,
        val monthlyNewClothes: Int = 0,

        // Vehicle Technical Data
        val engineSize: Double = 0.0,
        val fuelType: String = "",
        val co2Emissions: Double = 0.0,

        // Metadata
        val timestamp: String = "",
        val userId: String = ""
    )
