package com.quantasis.calllog.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.adapter.ContactCallInfoAdapter
import com.quantasis.calllog.ui.CallerDashboardActivity
import com.quantasis.calllog.viewModel.ContactsViewModel

class ContactsFragment : Fragment() {

    private lateinit var viewModel: ContactsViewModel
    private lateinit var adapter: ContactCallInfoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_contacts)
        adapter = ContactCallInfoAdapter{ contactInfo ->

            val intent = Intent(requireContext(), CallerDashboardActivity::class.java).apply {
                putExtra("name", contactInfo.contactName)
                putExtra("number", contactInfo.phone)
            }
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[ContactsViewModel::class.java]
        viewModel.contactCallInfoList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}