package com.tolgakurucay.mynotebook.views.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentGetImageFromAPIBinding
import com.tolgakurucay.mynotebook.viewmodels.main.GetImageViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GetImageFromAPIFragment : Fragment() {

    private lateinit var binding:FragmentGetImageFromAPIBinding
    private val viewModel:GetImageViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentGetImageFromAPIBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getImages()
    }
}