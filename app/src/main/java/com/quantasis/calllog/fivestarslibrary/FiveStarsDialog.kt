package com.quantasis.calllog.fivestarslibrary

import android.app.AlertDialog
import android.app.AlertDialog.Builder
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import com.quantasis.calllog.R


class FiveStarsDialog(private val context: Context, private var supportEmail: String) :
    DialogInterface.OnClickListener {
    private var isForceMode = false
    private val sharedPrefs: SharedPreferences
    private var contentTextView: TextView? = null
    private var ratingBar: RatingBar? = null
    private var title: String? = null
    private var rateText: String? = null
    private var alertDialog: AlertDialog? = null
    private var dialogView: View? = null
    private var upperBound = 4
    private var negativeReviewListener: NegativeReviewListener? = null
    private var reviewListener: ReviewListener? = null
    private var starColor = 0
    private var positiveButtonText: String? = "Ok"
    private var negativeButtonText: String? = "Not Now"
    private var neutralButtonText: String? = "Never"

    init {
        sharedPrefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    private fun build() {
        val builder: AlertDialog.Builder = Builder(context)
        val inflater = LayoutInflater.from(context)
        dialogView = inflater.inflate(R.layout.stars, null)
        val titleToAdd = if (title == null) DEFAULT_TITLE else title!!
        val textToAdd = if (rateText == null) DEFAULT_TEXT else rateText!!
        contentTextView = dialogView!!.findViewById<TextView>(R.id.text_content)
        contentTextView?.setText(textToAdd)
        ratingBar = dialogView!!.findViewById<RatingBar>(R.id.ratingBar)
        ratingBar?.setOnRatingBarChangeListener(OnRatingBarChangeListener { ratingBar, v, b ->
            Log.d(TAG, "Rating changed : $v")
            if (isForceMode && v >= upperBound) {
                openMarket()
                reviewListener?.onReview(ratingBar.rating.toInt())
            }
        })
        if (starColor != -1) {
            val stars = ratingBar?.getProgressDrawable() as LayerDrawable
            stars.getDrawable(1).setColorFilter(starColor, PorterDuff.Mode.SRC_ATOP)
            stars.getDrawable(2).setColorFilter(starColor, PorterDuff.Mode.SRC_ATOP)
        }
        builder.setTitle(titleToAdd)
            .setView(dialogView)
        if (negativeButtonText != null && !negativeButtonText!!.isEmpty()) builder.setNegativeButton(
            negativeButtonText,
            this
        )
        if (positiveButtonText != null && !positiveButtonText!!.isEmpty()) builder.setPositiveButton(
            positiveButtonText,
            this
        )
        if (neutralButtonText != null && !neutralButtonText!!.isEmpty()) builder.setNeutralButton(
            neutralButtonText,
            this
        )
        alertDialog = builder.create()
    }

    private fun disable() {
        val shared = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putBoolean(SP_DISABLED, true)
        editor.apply()
    }

    private fun openMarket() {
        val appPackageName = context.packageName
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$appPackageName")
            );
            intent.setPackage("com.android.vending")
            context.startActivity( intent )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.setType("text/email")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Report (" + context.packageName + ")")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        emailIntent.setPackage("com.google.android.gm")
        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."))
    }

    fun show(isForce: Boolean = false) {
        val disabled = sharedPrefs.getBoolean(SP_DISABLED, false)
        if (!disabled || isForce) {
            build()
            alertDialog?.show()
        }
    }

    fun showAfter(numberOfAccess: Int) {
        build()
        val editor = sharedPrefs.edit()
        val numOfAccess = sharedPrefs.getInt(SP_NUM_OF_ACCESS, 0)
        editor.putInt(SP_NUM_OF_ACCESS, numOfAccess + 1)
        editor.apply()
        if (numOfAccess + 1 >= numberOfAccess) {
            show()
        }
    }

    override fun onClick(dialogInterface: DialogInterface, i: Int) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            if (ratingBar!!.rating < upperBound) {
                if (negativeReviewListener == null) {
                    sendEmail()
                } else {
                    negativeReviewListener!!.onNegativeReview(ratingBar!!.rating.toInt())
                }
            } else if (!isForceMode) {
                openMarket()
            }
            disable()
            reviewListener?.onReview(ratingBar!!.rating.toInt())
        }
        if (i == DialogInterface.BUTTON_NEUTRAL) {
            disable()
        }
        if (i == DialogInterface.BUTTON_NEGATIVE) {
            val editor = sharedPrefs.edit()
            editor.putInt(SP_NUM_OF_ACCESS, 0)
            editor.apply()
        }
        alertDialog?.hide()
    }

    fun setTitle(title: String?): FiveStarsDialog {
        this.title = title
        return this
    }

    fun setSupportEmail(supportEmail: String): FiveStarsDialog {
        this.supportEmail = supportEmail
        return this
    }

    fun setRateText(rateText: String?): FiveStarsDialog {
        this.rateText = rateText
        return this
    }

    fun setStarColor(color: Int): FiveStarsDialog {
        starColor = color
        return this
    }

    fun setPositiveButtonText(positiveButtonText: String?): FiveStarsDialog {
        this.positiveButtonText = positiveButtonText
        return this
    }

    fun setNegativeButtonText(negativeButtonText: String?): FiveStarsDialog {
        this.negativeButtonText = negativeButtonText
        return this
    }

    fun setNeutralButton(neutralButtonText: String?): FiveStarsDialog {
        this.neutralButtonText = neutralButtonText
        return this
    }

    /**
     * Set to true if you want to send the user directly to the market
     *
     * @param isForceMode
     * @return
     */
    fun setForceMode(isForceMode: Boolean): FiveStarsDialog {
        this.isForceMode = isForceMode
        return this
    }

    /**
     * Set the upper bound for the rating.
     * If the rating is >= of the bound, the market is opened.
     *
     * @param bound the upper bound
     * @return the dialog
     */
    fun setUpperBound(bound: Int): FiveStarsDialog {
        upperBound = bound
        return this
    }

    /**
     * Set a custom listener if you want to OVERRIDE the default "send email" action when the user gives a negative review
     *
     * @param listener
     * @return
     */
    fun setNegativeReviewListener(listener: NegativeReviewListener?): FiveStarsDialog {
        negativeReviewListener = listener
        return this
    }

    /**
     * Set a listener to get notified when a review (positive or negative) is issued, for example for tracking purposes
     *
     * @param listener
     * @return
     */
    fun setReviewListener(listener: ReviewListener?): FiveStarsDialog {
        reviewListener = listener
        return this
    }

    companion object {
        private const val DEFAULT_TITLE = "Rate this app"
        private const val DEFAULT_TEXT = "How much do you love our app?"
        private const val SP_NUM_OF_ACCESS = "numOfAccess"
        private const val SP_DISABLED = "disabled"
        private val TAG = FiveStarsDialog::class.java.simpleName
    }
}