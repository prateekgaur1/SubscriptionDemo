package com.soulside.userapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.soulside.userapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var billingClient : BillingClient
    private lateinit var binding: ActivityMainBinding

    companion object{
        const val TAG = "MainActivity"
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            showOnUi(TAG, "purchasesUpdatedListener: billingResult = $billingResult")
        }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView.setOnClickListener {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                        showOnUi(TAG, "onBillingSetupFinished: result Code OK, Querying Products...")
                        queryProductsAvailableToPurchase()
                    }
                    else showOnUi(TAG, "onBillingSetupFinished: ERROR result = $billingResult")
                }
                override fun onBillingServiceDisconnected() {
                    showOnUi(TAG, "onBillingServiceDisconnected: ")
                }
            })
        }

         billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
             .enablePendingPurchases()
            .build()
    }

    private fun queryProductsAvailableToPurchase() {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("soulside-groups-annual")
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build(),
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("soulside-groups-weekly")
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build()
                    )
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            showOnUi(TAG, "queryProductsAvailableToPurchase: billing Result = $billingResult")
            showOnUi(TAG, "queryProductsAvailableToPurchase: products = $productDetailsList")
        }
    }

    private fun showOnUi(tag : String, message: String){
        runOnUiThread {
            binding.textView.text = binding.textView.text.toString() + "\n\n$tag: $message"
        }
        Log.d(tag, message)
    }
}