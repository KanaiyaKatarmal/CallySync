package com.quantasis.calllog.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.quantasis.calllog.R
import com.quantasis.calllog.fivestarslibrary.FiveStarsDialog
import com.quantasis.calllog.ui.DriveBackupActivity
import com.quantasis.calllog.ui.LocalBackupActivity


class MoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val version = requireContext().packageManager
            .getPackageInfo(requireContext().packageName, 0).versionName

        view.findViewById<TextView>(R.id.appversion).text = version;


        view.findViewById<LinearLayout>(R.id.setting_backup).setOnClickListener {
            startActivity(Intent(context,LocalBackupActivity::class.java))
        }

        view.findViewById<LinearLayout>(R.id.setting_cloudbackup).setOnClickListener {
            startActivity(Intent(context,DriveBackupActivity::class.java))
        }



        view.findViewById<LinearLayout>(R.id.setting_settings).setOnClickListener {
            showToast("This is in Progress")
        }

        view.findViewById<LinearLayout>(R.id.setting_dialer).setOnClickListener {
            showToast("This is in Progress")
        }

        view.findViewById<LinearLayout>(R.id.setting_subscriptions).setOnClickListener {
            showToast("This is in Progress")
        }

        view.findViewById<LinearLayout>(R.id.setting_support).setOnClickListener {
            openGmail("Support");
        }

        view.findViewById<LinearLayout>(R.id.setting_suggestions).setOnClickListener {
            openGmail("Suggestions");
        }

        view.findViewById<LinearLayout>(R.id.setting_bug_report).setOnClickListener {
            openGmail("Bug");
        }

        view.findViewById<LinearLayout>(R.id.setting_share).setOnClickListener {
            shareIntent();
        }

        view.findViewById<LinearLayout>(R.id.setting_rate_app).setOnClickListener {
            //redirectToPlayStore();

            val fiveStarsDialog = FiveStarsDialog(requireContext(), "abc@gmail.com")
            fiveStarsDialog.setRateText("Taking a few seconds to rate an app is vey important for the developer.")
                .setTitle("Please Rate the App")
                .setForceMode(false)
                //.setStarColor(Color.YELLOW)
                .setUpperBound(4) // Market opened if a rating >= 4 is selected
                .setNegativeReviewListener(null) // OVERRIDE mail intent for negative review
                .setReviewListener(null) // Used to listen for reviews (if you want to track them )
                .show(true)
        }

        view.findViewById<LinearLayout>(R.id.setting_privacy).setOnClickListener {
            showToast("Privacy Policy clicked")
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    fun openGmail(title:String) {
        try {

            val version = requireContext().packageManager
                .getPackageInfo(requireContext().packageName, 0).versionName

            val intent = Intent("android.intent.action.SEND")
            intent.putExtra(
                "android.intent.extra.EMAIL",
                arrayOf<String>(getString(R.string.toEmail))
            )
            val sb = StringBuilder()
            sb.append(resources.getString(R.string.app_name))
            sb.append(", $title ")
            sb.append(", Version: ")
            sb.append(version)
            intent.putExtra("android.intent.extra.SUBJECT", sb.toString())
            intent.putExtra("android.intent.extra.TEXT", "")
            intent.setType("text/html")
            intent.setPackage("com.google.android.gm")
            startActivity(Intent.createChooser(intent, "Send mail"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun redirectToPlayStore() {
        val str = "android.intent.action.VIEW"
        val packageName: String? = context?.packageName
        try {
            val sb = java.lang.StringBuilder()
            sb.append("market://details?id=")
            sb.append(packageName)
            startActivity(Intent(str, Uri.parse(sb.toString())))
        } catch (unused: ActivityNotFoundException) {
            val sb2 = java.lang.StringBuilder()
            sb2.append("https://play.google.com/store/apps/details?id=")
            sb2.append(packageName)
            startActivity(Intent(str, Uri.parse(sb2.toString())))
        }
    }

    fun shareIntent() {
        try {
            val intent = Intent("android.intent.action.SEND")
            intent.setType("text/plain")
            intent.putExtra("android.intent.extra.SUBJECT", resources.getString(R.string.app_name))
            val sb = java.lang.StringBuilder()
            sb.append("\nLet me recommend you this application\n\n")
            sb.append("https://play.google.com/store/apps/details?id=")
            sb.append(context?.getPackageName())
            sb.append("\n\n")
            intent.putExtra("android.intent.extra.TEXT", sb.toString())
            startActivity(Intent.createChooser(intent, "Share"))
        } catch (unused: java.lang.Exception) {
        }
    }
}