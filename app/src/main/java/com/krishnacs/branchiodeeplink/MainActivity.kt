package com.krishnacs.branchiodeeplink

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.krishnacs.branchiodeeplink.databinding.MainActivityBinding
import com.krishnacs.branchiodeeplink.ui.theme.BranchIODeeplinkTheme
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.util.LinkProperties
import io.branch.referral.validators.IntegrationValidator
import java.util.UUID

class MainActivity : ComponentActivity() {
    private lateinit var binding: MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clickmm.setOnClickListener{
            val branchUniversalObject = BranchUniversalObject()
                .setCanonicalIdentifier("item/12345")
                .setTitle("My Product")
                .setContentDescription("This is a great product!")
                .setContentImageUrl("hhttps://www.istockphoto.com/photos/mobile-phone")

            val linkProperties = LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .setCampaign("product launch")
                .addControlParameter("\$desktop_url", "https://f22labs.com")
                .addControlParameter("\$ios_url", "https://f22labs.com")
            Log.e("Hello","$linkProperties, $branchUniversalObject")
            showShareView(linkProperties, branchUniversalObject)
        }




    }
    private fun showShareView(linkProperties: LinkProperties, branchUniversalObject: BranchUniversalObject) {
        branchUniversalObject.generateShortUrl(this, linkProperties, Branch.BranchLinkCreateListener { url, error ->
            if (error == null) {
                Log.v(TAG, "\"got my Branch link to share: " + url)
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, url)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(Intent.createChooser(intent, "Share trailer!"))
            } else {
                Log.e(TAG, "onLinkCreate: Branch error: " + error.message)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        IntegrationValidator.validate(this)
        Branch.sessionBuilder(this).withCallback { branchUniversalObject, linkProperties, error ->
            if (error != null) {
                Log.e("BranchSDK_Tester", "branch init failed. Caused by -" + error.message)
            } else {
                Log.i("BranchSDK_Tester", "branch init complete!")
                if (branchUniversalObject != null) {
                    Log.i("BranchSDK_Tester", "title " + branchUniversalObject.title)
                    Log.i("BranchSDK_Tester", "CanonicalIdentifier " + branchUniversalObject.canonicalIdentifier)
                    Log.i("BranchSDK_Tester", "metadata " + branchUniversalObject.contentMetadata.convertToJson())
                }
                if (linkProperties != null) {
                    Log.i("BranchSDK_Tester", "Channel " + linkProperties.channel)
                    Log.i("BranchSDK_Tester", "control params " + linkProperties.controlParams)
                }
            }
        }.withData(this.intent.data).init()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.setIntent(intent);
        if (intent != null && intent.hasExtra("branch_force_new_session") && intent.getBooleanExtra("branch_force_new_session",false)) {
            Branch.sessionBuilder(this).withCallback { referringParams, error ->
                if (error != null) {
                    Log.e("BranchSDK_Tester", error.message)
                } else if (referringParams != null) {
                    Log.i("BranchSDK_Tester", referringParams.toString())
                }
            }.reInit()
        }
    }
}