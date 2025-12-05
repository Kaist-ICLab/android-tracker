package kaist.iclab.mobiletracker.viewmodels.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.mobiletracker.data.campaign.CampaignData
import kaist.iclab.mobiletracker.repository.Result
import kaist.iclab.mobiletracker.services.CampaignService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountSettingsViewModel(
    private val campaignService: CampaignService
) : ViewModel() {
    private val TAG = "AccountSettingsViewModel"
    
    // Campaign state
    private val _campaigns = MutableStateFlow<List<CampaignData>>(emptyList())
    val campaigns: StateFlow<List<CampaignData>> = _campaigns.asStateFlow()
    
    private val _isLoadingCampaigns = MutableStateFlow(false)
    val isLoadingCampaigns: StateFlow<Boolean> = _isLoadingCampaigns.asStateFlow()
    
    private val _campaignError = MutableStateFlow<String?>(null)
    val campaignError: StateFlow<String?> = _campaignError.asStateFlow()
    
    // Selected campaign ID (stored as String for UI compatibility)
    private val _selectedCampaignId = MutableStateFlow<String?>(null)
    val selectedCampaignId: StateFlow<String?> = _selectedCampaignId.asStateFlow()
    
    /**
     * Fetch all campaigns from Supabase
     * Only fetches if campaigns list is empty and not already loading
     */
    fun fetchCampaigns() {
        if (_campaigns.value.isNotEmpty() || _isLoadingCampaigns.value) {
            return
        }
        
        viewModelScope.launch {
            _isLoadingCampaigns.value = true
            _campaignError.value = null
            
            when (val result = campaignService.getAllCampaigns()) {
                is Result.Success -> {
                    _campaigns.value = result.data
                    _isLoadingCampaigns.value = false
                }
                is Result.Error -> {
                    _campaignError.value = result.message
                    _isLoadingCampaigns.value = false
                    Log.e(TAG, "Error fetching campaigns: ${result.message}", result.exception)
                }
            }
        }
    }
    
    /**
     * Select a campaign by ID
     * @param campaignId The campaign ID to select (as String)
     */
    fun selectCampaign(campaignId: String) {
        _selectedCampaignId.value = campaignId
    }
    
    /**
     * Clear selected campaign
     */
    fun clearSelectedCampaign() {
        _selectedCampaignId.value = null
    }
    
    /**
     * Get the selected campaign name
     * @return The campaign name if selected, null otherwise
     */
    fun getSelectedCampaignName(): String? {
        val selectedId = _selectedCampaignId.value ?: return null
        return _campaigns.value.find { it.idString == selectedId }?.name
    }
}

