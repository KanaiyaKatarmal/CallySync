package com.quantasis.calllog.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.quantasis.calllog.R

class MoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<LinearLayout>(R.id.setting_backup).setOnClickListener {
            showToast("Settings Backup")
        }

        view.findViewById<LinearLayout>(R.id.setting_settings).setOnClickListener {
            showToast("Settings clicked")
        }

        view.findViewById<LinearLayout>(R.id.setting_dialer).setOnClickListener {
            showToast("Make Default Dialer clicked")
        }

        view.findViewById<LinearLayout>(R.id.setting_subscriptions).setOnClickListener {
            showToast("Subscriptions clicked")
        }

        view.findViewById<LinearLayout>(R.id.setting_support).setOnClickListener {
            showToast("Help & Support clicked")
        }

        view.findViewById<LinearLayout>(R.id.setting_suggestions).setOnClickListener {
            showToast("Suggestions clicked")
        }

        view.findViewById<LinearLayout>(R.id.setting_bug_report).setOnClickListener {
            showToast("Bugs Report clicked")
        }

        view.findViewById<LinearLayout>(R.id.setting_share).setOnClickListener {
            showToast("Share clicked")
        }

        view.findViewById<LinearLayout>(R.id.setting_rate_app).setOnClickListener {
            showToast("Rate App clicked")
        }

        view.findViewById<LinearLayout>(R.id.setting_privacy).setOnClickListener {
            showToast("Privacy Policy clicked")
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}