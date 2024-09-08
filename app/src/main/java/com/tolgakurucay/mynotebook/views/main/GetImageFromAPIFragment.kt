package com.tolgakurucay.mynotebook.views.main


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.tolgakurucay.mynotebook.adapters.ImageAdapter
import com.tolgakurucay.mynotebook.databinding.FragmentGetImageFromAPIBinding
import com.tolgakurucay.mynotebook.models.ImageResponse
import com.tolgakurucay.mynotebook.viewmodels.main.GetImageViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GetImageFromAPIFragment : Fragment() {

    private lateinit var binding: FragmentGetImageFromAPIBinding
    private val viewModel: GetImageViewModel by viewModels()
    val TAG = "bilgi"
    private lateinit var adapter: ImageAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGetImageFromAPIBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        buttonChangeListener()
        observeLiveData()
    }

    private fun buttonChangeListener() {
        binding.ImageNameText.addTextChangedListener {
            Log.d(TAG, "buttonChangeListener: ${it.toString()}")
            viewModel.getImages(it.toString())
        }
    }

    private fun observeLiveData() {
        viewModel.responseFromAPI.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d(TAG, "observeLiveData: $it")
                adapter.updateAdapter(it)
            }
        })
    }

    private fun init() {
        adapter = ImageAdapter(ImageResponse(arrayListOf(), 0, 0))
        binding.imageRecycler.adapter = adapter
        binding.imageRecycler.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
    }
}