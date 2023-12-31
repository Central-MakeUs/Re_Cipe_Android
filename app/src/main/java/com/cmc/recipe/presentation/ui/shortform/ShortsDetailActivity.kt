package com.cmc.recipe.presentation.ui.shortform

import BottomSheetCommentFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.cmc.recipe.R
import com.cmc.recipe.data.model.ExoPlayerItem
import com.cmc.recipe.data.model.ShortsMapper.toContent
import com.cmc.recipe.data.model.response.CommentContent
import com.cmc.recipe.data.model.response.ReviewContent
import com.cmc.recipe.data.model.response.ShortsContent
import com.cmc.recipe.data.source.remote.request.CommentRequest
import com.cmc.recipe.databinding.ActivityShortsDetailBinding
import com.cmc.recipe.presentation.ui.MainActivity
import com.cmc.recipe.presentation.ui.common.CommentAdapter
import com.cmc.recipe.presentation.ui.common.OnCommentListener
import com.cmc.recipe.presentation.ui.common.RecipeSnackBar
import com.cmc.recipe.presentation.ui.recipe.BottomSheetDetailDialog
import com.cmc.recipe.presentation.viewmodel.CommentViewModel
import com.cmc.recipe.presentation.viewmodel.ShortsViewModel
import com.cmc.recipe.utils.NetworkState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ShortsDetailActivity : AppCompatActivity() {
    private val shortsViewModel : ShortsViewModel by viewModels()
    private val commentViewModel : CommentViewModel by viewModels()

    private lateinit var binding: ActivityShortsDetailBinding
    private val exoPlayerItems = ArrayList<ExoPlayerItem>()
    private var currentPosition = 0
    private var currentId = 0

    private var isMute = false

    private lateinit var adapter : ShortsDetailAdapter
    private lateinit var commnetAdapter : CommentAdapter
    private lateinit var dialog : BottomSheetCommentFragment
    private lateinit var commentItemList : ArrayList<CommentContent>

    private var isCommentInitialized : Boolean? = null
    private var favoriteFlag : Boolean? = null
    private var saveFlag : Boolean? = null
    private var itemSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShortsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //position 전달 받음
        if(intent.hasExtra("detailId")){
            currentPosition = intent.getIntExtra("detailId",0)
            requestRecipeDetail(currentPosition)
        }else{
            currentPosition = intent.getIntExtra("position",0)
            requestRecipeList()
        }

        initMenu()
    }

    override fun onPause() {
        super.onPause()

        val index = exoPlayerItems.indexOfFirst { it.position == binding.vpExoplayer.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.pause()
            player.playWhenReady = false
        }
    }

    override fun onResume() {
        super.onResume()

        val index = exoPlayerItems.indexOfFirst { it.position == binding.vpExoplayer.currentItem }
        Log.d("onResume ","${index}")
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.playWhenReady = true
            player.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (exoPlayerItems.isNotEmpty()) {
            for (item in exoPlayerItems) {
                val player = item.exoPlayer
                player.clearMediaItems()
                player.release()
            }
            exoPlayerItems.clear()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun requestRecipeList(){
        shortsViewModel.getRecipesShortform()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shortsViewModel.recipeShortsResult.collect { response ->
                    when (response) {
                        is NetworkState.Success -> {
                            response.data?.let { data ->
                                if (data.code == "SUCCESS") {
                                    itemSize = response.data.data.content.size
                                    initVideo(response.data.data.content as ArrayList<ShortsContent>)
                                }
                            }
                            shortsViewModel._recipeShortsResult.value = NetworkState.Loading
                        }
                        is NetworkState.Error -> {
                            shortsViewModel._recipeShortsResult.value = NetworkState.Loading
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun requestRecipeDetail(id:Int) {
        shortsViewModel.getRecipesShortformDetail(id)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shortsViewModel.recipeShortsDetailResult.collect { response ->
                    when (response) {
                        is NetworkState.Success -> {
                            response.data?.let { data ->
                                if (data.code == "SUCCESS") {
                                    itemSize = 1
                                    val itemList = arrayListOf(response.data.data.toContent())
                                    initVideo(itemList)
                                }
                            }
                            shortsViewModel._recipeShortsResult.value = NetworkState.Loading
                        }
                        is NetworkState.Error -> {
                            shortsViewModel._recipeShortsResult.value = NetworkState.Loading
                        }
                        else -> {}
                    }
                }
            }
        }
    }
    private fun initMenu(){

        binding.let {
            it.btnSpeak.bringToFront()
            it.btnBack.bringToFront()
            it.btnMore.bringToFront()
        }

        binding.btnBack.setOnClickListener {
            this.onBackPressed()
        }

        binding.btnMore.setOnClickListener {
            showBottomSheet()
        }

        binding.btnSpeak.setOnClickListener {
            if(!isMute){ // mute 아님
                binding.btnSpeak.setImageResource(R.drawable.ic_mute)
                exoPlayerItems[currentPosition].exoPlayer.volume = 0f
                isMute = true
            }else{
                binding.btnSpeak.setImageResource(R.drawable.ic_speak)
                exoPlayerItems[currentPosition].exoPlayer.volume = 0.5f
                isMute = false
            }
        }

    }

    private fun showBottomSheet(){
        val dialog = BottomSheetDetailDialog()
        dialog.setReportListener {   //신고하기
            requestReport(currentId)
        }
        dialog.setNoshowListener {// 관심없음
            postReviewNoInterest(currentId)
        }
        dialog.show(supportFragmentManager,"RemoveBottomSheetFragment")
    }


    private fun initVideo(itemList:ArrayList<ShortsContent>){
        adapter = ShortsDetailAdapter(applicationContext,object : ShortsItemHolder.OnVideoPreparedListener {
            override fun onVideoPrepared(exoPlayerItem: ExoPlayerItem) {
                exoPlayerItems.add(exoPlayerItem)

                if (exoPlayerItems.size == 1) {
                    exoPlayerItem.exoPlayer.playWhenReady = true
                    exoPlayerItem.exoPlayer.play()
                }
            }
        })


        adapter.setShortsListener(object : onShortsListener{
            override fun onFavorite(id:Int) {
                requestShortsLikeOrUnLike(id)
            }

            override fun onSave(id:Int) {
                requestShortsSaveOrUnSave(id)
            }

            override fun onComment(id:Int) {
                requestCommentList(id)
                isCommentInitialized = true
            }
        })

        binding.vpExoplayer.adapter = adapter
        adapter.replaceData(itemList)

        binding.vpExoplayer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.d("position","${position}")
                currentId = adapter.getData()[position].shortform_id

                val previousIndex = exoPlayerItems.indexOfFirst { it.exoPlayer.isPlaying }

                if (previousIndex != -1) {
                    val player = exoPlayerItems[previousIndex].exoPlayer
                    player.pause()
                    player.playWhenReady = false
                    player.seekTo(0)
                }
                val newIndex = exoPlayerItems.indexOfFirst { it.position == position }
                if (newIndex != -1) {
                    val player = exoPlayerItems[newIndex].exoPlayer
                    player.playWhenReady = true
                    player.play()
                    currentPosition = newIndex
                }
            }

        })
        binding.vpExoplayer.setCurrentItem(currentPosition, false)
    }

    private fun findShortsItemById(shortsId: Int): ShortsContent? {
        return adapter.getData().find { it.shortform_id == shortsId }
    }

    private fun findCommentItemById(shortsId: Int): CommentContent? {
        return commnetAdapter.getData().find { it.comment_id == shortsId }
    }


    private fun findShortsItemIndex(shortId: Int): Int? {
        val dataList = adapter.getData()
        for ((index, item) in dataList.withIndex()) {
            if (item.shortform_id == shortId) {
                return index
            }
        }
        return null
    }

    private fun findCommentItemIndex(shortId: Int): Int? {
        val dataList = commnetAdapter.getData()
        for ((index, item) in dataList.withIndex()) {
            if (item.comment_id == shortId) {
                return index
            }
        }
        return null
    }

    private fun requestCommentReport(id:Int) {
        commentViewModel.reportShortfromComment(id)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                commentViewModel.reportResult.collect{
                    when (it) {
                        is NetworkState.Success -> {
                            if (it.data.code == "SUCCESS") {
                                commnetAdapter.removeItem(id)
                                dialog.setCommentCount(commentItemList.size)
                                Toast.makeText(applicationContext, "신고가 접수되었습니다.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        is NetworkState.Error -> {
                            Toast.makeText(applicationContext, "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is NetworkState.Loading -> {
                            // 프로그레스바 띄우기
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun requestFavorite(id:Int) {
        shortsViewModel.postShortformLike(id)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shortsViewModel.shortsLikeResult.collect{
                    when (it) {
                        is NetworkState.Success -> {
                            if (it.data.code == "SUCCESS") {
                                val item = findShortsItemById(id)
                                val index = findShortsItemIndex(id)
                                item?.is_liked = true
                                item?.likes_count = item?.likes_count!! + 1
                                updateValueAtIndex(adapter.getData(),index!!,item)
                            }
                        }
                        is NetworkState.Error -> {
                            Toast.makeText(applicationContext, "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is NetworkState.Loading -> {
                            // 프로그레스바 띄우기
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun requestCommentList(id:Int) {
        commentViewModel.getShortfromComment(id)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                commentViewModel.commentResult.collect{
                    when (it) {
                        is NetworkState.Success -> {
                            if (it.data.code == "SUCCESS") {
                                commentItemList = it.data.data.content as ArrayList<CommentContent>

                                if(isCommentInitialized == true){
                                    showComment()
                                    isCommentInitialized = false
                                }else{
                                    dialog.setCommentCount(commentItemList.size)
                                    commnetAdapter.replaceData(commentItemList)
                                }
                            }
                            commentViewModel._commentResult.value = NetworkState.Loading
                        }

                        is NetworkState.Error -> {
                            Toast.makeText(applicationContext, "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                            commentViewModel._commentResult.value = NetworkState.Loading
                        }
                        is NetworkState.Loading -> {
                            // 프로그레스바 띄우기
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun requestCommentLikeOrUnLike(id: Int) {
        val review = findCommentItemById(id)
        if (review != null) {
            if (review.is_liked) {
                requestCommentUnLike(id)
            } else {
                requestCommentLike(id)
            }
        }
    }

    private fun requestCommentLike(id:Int){
        commentViewModel.postShortfromCommentLike(id)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                commentViewModel.commentLikeResult.collect {
                    when (it) {
                        is NetworkState.Success -> {
                            it.data?.let { data ->
                                if (data.code == "SUCCESS") {
                                    val item = findCommentItemById(id)
                                    val index = findCommentItemIndex(id)
                                    Log.d("item","${item}")
                                    item?.is_liked = true
                                    item?.comment_likes = item?.comment_likes!! + 1
                                    updateValueAtCommentIndex(commnetAdapter.getData(),index!!,item)
                                    commnetAdapter.notifyDataSetChanged()
                                    Log.d("item","${commnetAdapter.getData()}")
                                    dialog.setCommentCount(commnetAdapter.getData().size)
                                } else {
                                    Log.d("data", "${data.data}")
                                }
                            }
                        }
                        is NetworkState.Error -> {
                            Log.d("data", "${it.message}")

                            commentViewModel._commentLikeResult.emit(NetworkState.Loading)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun requestCommentUnLike(id:Int){
        commentViewModel.postShortfromCommentUnLike(id)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                commentViewModel.commentUnLikeResult.collect {
                    when (it) {
                        is NetworkState.Success -> {
                            it.data?.let { data ->
                                if (data.code == "SUCCESS") {
                                    val item = findCommentItemById(id)
                                    val index = findCommentItemIndex(id)
                                    item?.is_liked = false
                                    item?.comment_likes = item?.comment_likes!! - 1
                                    updateValueAtCommentIndex(commnetAdapter.getData(),index!!,item)
                                    dialog.setCommentCount(commnetAdapter.getData().size)
                                } else {
                                    Log.d("data", "${data.data}")
                                }
                            }
                            commentViewModel._commentUnLikeResult.emit(NetworkState.Loading)
                        }
                        is NetworkState.Error -> {
                            Log.d("data", "${it.message}")

                            commentViewModel._commentUnLikeResult.emit(NetworkState.Loading)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun showComment(){
        commnetAdapter = CommentAdapter()
        commnetAdapter.setCommentListener(object : OnCommentListener {
            override fun onFavorite(id: Int) {
                //좋아요 기능
                requestCommentLikeOrUnLike(id)
            }

            override fun onReport(id: Int) {
                requestCommentReport(id)
            }

            override fun writeReply(id: Int) {
            }

        })
        commnetAdapter.replaceData(commentItemList)

        dialog = BottomSheetCommentFragment(this,R.layout.bottom_sheet_comment,commnetAdapter,commentItemList)
        dialog.setListener(object : BottomSheetCommentFragment.onInputEventListener{
            override fun onEdit(data: String) {
               // 댓글 요청
                requestSaveComment(data)
            }
        })
        dialog.show()
    }

    fun updateValueAtIndex(list: MutableList<ShortsContent>, index: Int, newValue: ShortsContent) {
        if (index in 0 until list.size) {
            list[index] = newValue
        } else {
            0
        }
    }

    fun updateValueAtCommentIndex(list: MutableList<CommentContent>, index: Int, newValue: CommentContent) {
        if (index in 0 until list.size) {
            list[index] = newValue
        } else {
            0
        }
    }

    private fun requestSaveComment(data:String) {
        commentViewModel.postShortfromCommentSave(currentId,CommentRequest(data))
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                commentViewModel.commentSaveResult.collect{
                    when (it) {
                        is NetworkState.Success -> {
                            if (it.data.code == "SUCCESS") {
                                requestCommentList(currentId)
                                commnetAdapter.notifyDataSetChanged()
                            }
                        }
                        is NetworkState.Error -> {
                            Toast.makeText(applicationContext, "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is NetworkState.Loading -> {
                            // 프로그레스바 띄우기
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun requestUnFavorite(id:Int) {
        shortsViewModel.postShortformUnLike(id)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shortsViewModel.shortsUnLikeResult.collect{
                    when (it) {
                        is NetworkState.Success -> {
                            if (it.data.code == "SUCCESS") {
                                val item = findShortsItemById(id)
                                val index = findShortsItemIndex(id)
                                item?.is_liked = false
                                item?.likes_count = item?.likes_count!! - 1
                                updateValueAtIndex(adapter.getData(),index!!,item)
                            }
                        }
                        is NetworkState.Error -> {
                            Toast.makeText(applicationContext, "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is NetworkState.Loading -> {
                            // 프로그레스바 띄우기
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun requestReport(id:Int) {
        shortsViewModel.reportShortform(id)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shortsViewModel.reportResult.collect{
                    when (it) {
                        is NetworkState.Success -> {
                            if (it.data.code == "SUCCESS") {

                                val nextItem: Int = (currentPosition + 1) % itemSize
                                binding.vpExoplayer.setCurrentItem(nextItem, true)

                                itemSize--
                                if(itemSize == 0 ){
                                    exitActivity()
                                    Toast.makeText(applicationContext, "해당 숏폼은 신고 되었습니다", Toast.LENGTH_SHORT)
                                        .show()
                                }else{
                                    adapter.removeItem(currentId)
                                    Toast.makeText(applicationContext, "해당 숏폼은 신고 되었습니다", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                        is NetworkState.Error -> {
                            Toast.makeText(applicationContext, "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is NetworkState.Loading -> {
                            // 프로그레스바 띄우기
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun postReviewNoInterest(id:Int) {
        shortsViewModel.postReviewNoInterest(id)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shortsViewModel.noInterestResult.collect{
                    when (it) {
                        is NetworkState.Success -> {
                            if (it.data.code == "SUCCESS") {
                                val nextItem: Int = (currentPosition + 1) % itemSize
                                binding.vpExoplayer.setCurrentItem(nextItem, true)
                                itemSize--
                                if(itemSize == 0 ){
                                    exitActivity()
                                    Toast.makeText(applicationContext, "해당 숏폼은 관심없음 처리 되었습니다", Toast.LENGTH_SHORT)
                                        .show()
                                }else{
                                    adapter.removeItem(currentId)
                                    Toast.makeText(applicationContext, "해당 숏폼은 관심없음 처리 되었습니다", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                        is NetworkState.Error -> {
                            Toast.makeText(applicationContext, "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is NetworkState.Loading -> {
                            // 프로그레스바 띄우기
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun exitActivity(){
        finish()
    }
    fun requestShortsLikeOrUnLike(id: Int) {
        val item = findShortsItemById(id)
        if(favoriteFlag == null){
            favoriteFlag = item!!.is_liked
        }
        if (item != null) {
            if (!favoriteFlag!!) {
                requestFavorite(id)
                favoriteFlag = true
            } else {
                requestUnFavorite(id)
                favoriteFlag = false
            }
        }
    }

    private fun requestShortsSaveOrUnSave(id: Int) {
        val item = findShortsItemById(id)

        if(saveFlag == null){
            saveFlag = item!!.is_saved
        }
        if (item != null) {
            if (!saveFlag!!) {
                requestSave(id)
                saveFlag = true
            } else {
                requestUnSave(id)
                saveFlag = false
            }
        }
    }

    private fun requestSave(id:Int) {
        shortsViewModel.postShortformSave(id)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shortsViewModel.shortsSaveResult.collect {
                    when (it) {
                        is NetworkState.Success -> {
                            if (it.data.code == "SUCCESS") {
                                val item = findShortsItemById(id)
                                val index = findShortsItemIndex(id)
                                item?.is_saved = true
                                item?.saved_count = item?.saved_count!! + 1
                                updateValueAtIndex(adapter.getData(),index!!,item)
                            }
                        }
                        is NetworkState.Error -> {
                            //showToastMessage("${it}")
                        }
                        is NetworkState.Loading -> {
                            // 프로그레스바 띄우기
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun requestUnSave(id:Int) {
        shortsViewModel.postShortformUnSave(id)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shortsViewModel.shortsUnSaveResult.collect {
                    when (it) {
                        is NetworkState.Success -> {
                            if (it.data.code == "SUCCESS") {
                                val item = findShortsItemById(id)
                                val index = findShortsItemIndex(id)
                                item?.is_saved = false
                                item?.saved_count = item?.saved_count!! - 1
                                updateValueAtIndex(adapter.getData(),index!!,item)
                            }
                            //showToastMessage("${it.data}")
                        }
                        is NetworkState.Error -> {
                           // showToastMessage("${it}")
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }
}

