package com.quantasis.calllog.fivestarslibrary


interface NegativeReviewListener {
    fun onNegativeReview(stars: Int)
}


interface ReviewListener {
    fun onReview(stars: Int)
}