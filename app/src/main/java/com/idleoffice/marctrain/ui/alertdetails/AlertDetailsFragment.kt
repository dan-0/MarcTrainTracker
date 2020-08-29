package com.idleoffice.marctrain.ui.alertdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.databinding.FragmentAlertDetailsBinding
import com.idleoffice.marctrain.util.extensions.setGone
import com.idleoffice.marctrain.util.extensions.setVisible
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlertDetailsFragment : Fragment() {

    private val viewModel: AlertDetailsViewModel by viewModel()

    private var _binding: FragmentAlertDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: AlertDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlertDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.state.collect {
                when (it) {
                    AlertDetailsViewState.Init -> viewModel.loadDetails(args.targetUrl)
                    AlertDetailsViewState.Loading -> showLoading()
                    AlertDetailsViewState.Error -> showError()
                    is AlertDetailsViewState.Content -> showContent(it.alertDetails)
                }
            }
        }
    }

    private fun showLoading() {
        binding.loadingLayout.loadingTextViewPartial.text = getString(R.string.loading_alert_details)
        binding.loadingLayout.root.setVisible()
        binding.content.setGone()
    }

    private fun showError() {
        binding.loadingLayout.loadingTextViewPartial.text = getString(R.string.error_loading_alert_details)
        binding.loadingLayout.root.setVisible()
        binding.content.setGone()
    }

    private fun showContent(alertDetails: AlertDetails) {
        with(binding) {
            title.text = alertDetails.title
            routes.text = alertDetails.affectedRoutes
            description.text = alertDetails.description
            startDate.text = alertDetails.startDate
            endDate.text = alertDetails.endDate
            cause.text = alertDetails.cause
            effect.text = alertDetails.effect
            content.setVisible()
        }

        binding.loadingLayout.root.setGone()
    }
}

