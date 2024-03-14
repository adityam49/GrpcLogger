package com.ducktappedapps.grpclogger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ducktappedapps.grpclogger.data.CallState
import com.ducktappedapps.grpclogger.data.LocalDataStore
import com.ducktappedapps.grpclogger.data.Log
import com.ducktappedapps.grpclogger.data.LogsDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

internal interface GrpcLoggingViewModel {
    val logs: StateFlow<PagingData<Log>>
    val detailedLogs: StateFlow<PagingData<Log>>
    val logSortedByAscendingOrder: StateFlow<Boolean>
    val sharingTextFlow: SharedFlow<String>
    val loggingEnabled: StateFlow<Boolean>

    fun clearLogs()
    fun showDetailedLogsFor(callId: String)
    fun flipSorting()
    fun shareText(logs: List<Log>)
    fun toggleLogging()
}

internal class GrpcLoggingViewModelImpl @Inject constructor(
    private val logsDao: LogsDao,
    private val localDataStore: LocalDataStore,
    private val defaultDispatcher: CoroutineDispatcher,
) : GrpcLoggingViewModel, ViewModel() {
    override val logs: MutableStateFlow<PagingData<Log>> = MutableStateFlow(PagingData.empty())

    override val detailedLogs: MutableStateFlow<PagingData<Log>> =
        MutableStateFlow(PagingData.empty())

    override val sharingTextFlow: MutableSharedFlow<String> = MutableSharedFlow()

    override val logSortedByAscendingOrder: MutableStateFlow<Boolean> =
        MutableStateFlow(false)

    override val loggingEnabled: StateFlow<Boolean> = localDataStore
        .logsEnabled()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)


    init {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = true,
                    maxSize = 100
                ),
                pagingSourceFactory = { logsDao.getPagedAllRequests() }
            )
                .flow
                .cachedIn(viewModelScope)
                .collect {
                    logs.value = it
                }
        }
    }

    override fun clearLogs() {
        viewModelScope.launch {
            logsDao.deleteAll()
        }
    }

    private var detailedLogsJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun showDetailedLogsFor(callId: String) {
        detailedLogsJob?.cancel()
        detailedLogsJob = viewModelScope.launch {
            logSortedByAscendingOrder
                .flatMapLatest { isAscending ->
                    Pager(
                        config = PagingConfig(
                            pageSize = 20, // Define the number of items per page
                            enablePlaceholders = true,
                            maxSize = 100
                        ),
                        pagingSourceFactory = {
                            if (isAscending)
                                logsDao.observeLogsForCallIdAscending(callId)
                            else
                                logsDao.observeLogsForCallIdDescending(callId)
                        }
                    ).flow
                }
                .cachedIn(viewModelScope)
                .collect {
                    detailedLogs.emit(it)
                }
        }
    }

    override fun shareText(logs: List<Log>) {
        viewModelScope.launch(defaultDispatcher) {
            sharingTextFlow.emit(logs.joinToString { it.data })
        }
    }

    override fun toggleLogging() {
        viewModelScope.launch {
            localDataStore.toggleLogging()
        }
    }

    override fun flipSorting() {
        viewModelScope.launch {
            logSortedByAscendingOrder.emit(!logSortedByAscendingOrder.value)
        }
    }
}

class FakeGrpcLoggingViewModel() : GrpcLoggingViewModel {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    override val logs: MutableStateFlow<PagingData<Log>> = MutableStateFlow(PagingData.empty())
    override val detailedLogs: MutableStateFlow<PagingData<Log>> =
        MutableStateFlow(PagingData.empty())
    override val logSortedByAscendingOrder: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val sharingTextFlow: MutableSharedFlow<String> = MutableSharedFlow()
    override val loggingEnabled: StateFlow<Boolean> = MutableStateFlow(false)

    init {
        coroutineScope.launch {
            logs.emit(
                PagingData.from(
                    buildList { repeat(50) { add(CallState.REQUEST) } }
                        .mapIndexed { index, callType ->
                            Log(
                                timestamp = System.currentTimeMillis(),
                                data = longResponse,
                                callId = "342",
                                callState = callType,
                                uid = index,
                            )
                        })
            )
        }


    }

    override fun clearLogs() {
        logs.value = PagingData.empty()
    }

    override fun toggleLogging() {

    }

    override fun showDetailedLogsFor(callId: String) {
        detailedLogs.value = PagingData.from(buildList {
            listOf(
                CallState.REQUEST,
                CallState.HEADERS,
                CallState.RESPONSE,
                CallState.RESPONSE,
                CallState.RESPONSE,
                CallState.CLOSE
            ).mapIndexed { index, callType ->
                add(
                    Log(
                        timestamp = System.currentTimeMillis(),
                        data = longResponse,
                        callId = callId,
                        callState = callType,
                        uid = index
                    )
                )
            }
        })
    }

    override fun flipSorting() {
        logSortedByAscendingOrder.value = !logSortedByAscendingOrder.value
    }

    override fun shareText(logs: List<Log>) {

    }

    companion object {
        const val longResponse = """{
  "orderId": "FD123456789",
  "partnerId": "PRT12345",
  "partnerName": "Gourmet Meals on Wheels",
  "orderStatus": "Delivered",
  "orderDate": "2024-03-13T14:22:31.000Z",
  "estimatedDeliveryTime": "2024-03-13T15:00:00.000Z",
  "actualDeliveryTime": "2024-03-13T14:58:00.000Z",
  "customerDetails": {
    "customerId": "CUST123456",
    "name": "Jane Doe",
    "phone": "+1234567890",
    "email": "jane.doe@example.com",
    "deliveryAddress": "123 Main St, Anytown, AT 12345"
  },
  "orderItems": [
    {
      "itemId": "ITEM123456",
      "name": "Margherita Pizza",
      "quantity": 1,
      "price": 12.99
    },
    {
      "itemId": "ITEM123457",
      "name": "Vegan Burger",
      "quantity": 2,
      "price": 9.99
    }
  ],
  "paymentStatus": "Paid",
  "paymentMethod": "Credit Card",
  "subtotal": 32.97,
  "tax": 2.64,
  "deliveryFee": 5.00,
  "total": 40.61,
  "riderDetails": {
    "riderId": "RIDER12345",
    "name": "John Smith",
    "phone": "+1234567891"
  },
  "deliveryInstructions": "Leave at front door",
  "partnerRating": 4.5,
  "customerRatingGiven": 5,
  "customerComment": "Quick delivery, great food!",
  "orderConfirmationTime": "2024-03-13T14:23:00.000Z",
  "orderPreparationTime": 20,
  "orderPreparationStartTime": "2024-03-13T14:25:00.000Z",
  "orderPreparationEndTime": "2024-03-13T14:45:00.000Z",
  "pickupTime": "2024-03-13T14:50:00.000Z",
  "riderPickupTime": "2024-03-13T14:55:00.000Z",
  "deliveryRoute": "Main St -> Elm St -> Destination",
  "expectedRouteDuration": 25,
  "actualRouteDuration": 23,
  "trafficCondition": "Moderate",
  "weatherCondition": "Sunny",
  "temperature": "25C",
  "partnerLocation": {
    "latitude": "40.712776",
    "longitude": "-74.005974"
  },
  "customerLocation": {
    "latitude": "40.715981",
    "longitude": "-74.002982"
  },
  "orderCancellationTime": null,
  "cancellationReason": null,
  "refundAmount": 0,
  "couponUsed": false,
  "discountApplied": 0,
  "loyaltyPointsUsed": 0,
  "loyaltyPointsEarned": 10,
  "specialInstructions": "Extra napkins, please",
  "isFirstOrder": false,
  "deviceUsedForOrder": "iPhone 12",
  "appVersion": "1.4.3",
  "partnerResponseTime": "2 mins",
  "orderPackagingTime": "5 mins",
  "sustainabilityRating": 4,
  "foodQualityRating": 5,
  "deliveryPackagingRating": 4.5,
  "orderAccuracyRating": 5,
  "timeToDeliveryRating": 5,
  "customerSupportRating": 4.5,
  "reorderRate": "30%",
  "averageDeliveryTime": "22 mins",
  "peakDeliveryTime": "18:00-20:00",
  "numberofOrdersDelivered": 1200,
  "numberofActivePartners": 300,
  "averageOrderValue": 35.50,
  "highestOrderValue": 150.75,
  "lowestOrderValue": 8.99,
  "commonFeedback": [
    "Fast delivery",
    "Excellent food quality",
    "Friendly rider"
  ],
  "upcomingPromotions": [
    {
      "promotionId": "PROMO123",
      "description": "20% off on next order",
      "validity": "2024-03-20"
    }
  ],
  "appImprovements": [
    "Faster checkout process",
    "More diverse menu options",
    "Loyalty rewards program"
  ]
}
"""

    }

}