
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quantasis.calllog.R
import com.quantasis.calllog.adapter.AllCallLogAdapter
import com.quantasis.calllog.viewModel.AllCallLogViewModel
import androidx.fragment.app.viewModels

class AllCallTabFragment : Fragment() {
    private val viewModel: AllCallLogViewModel by viewModels()
    private lateinit var adapter: AllCallLogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.all_call_tab_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView: RecyclerView = view.findViewById(R.id.callLogRecyclerView)
        val progressBar: View = view.findViewById(R.id.progressBar)
        val emptyTextView: View = view.findViewById(R.id.emptyTextView)

        adapter = AllCallLogAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Initially show loader
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyTextView.visibility = View.GONE

        viewModel.allCallLogs?.observe(viewLifecycleOwner) { logs ->
            progressBar.visibility = View.GONE
            if (!logs.isNullOrEmpty()) {
                adapter.setData(logs)
                recyclerView.visibility = View.VISIBLE
                emptyTextView.visibility = View.GONE
            } else {
                recyclerView.visibility = View.GONE
                emptyTextView.visibility = View.VISIBLE
            }
        }
    }
}