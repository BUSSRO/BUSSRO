package com.youreye.bussro.feature.detailinfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityDetailInfoBinding
import com.youreye.bussro.feature.dialog.SuggestionsDialog
import com.youreye.bussro.model.DetailInfoData
import java.util.*
import kotlin.collections.HashMap

class DetailInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailInfoBinding
    private lateinit var rvAdapter: DetailInfoAdapter
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_info)
        overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out)

        initClickListener()
        initVar()
    }

    private fun initClickListener() {
        /* 뒤로가기 */
        binding.ivDetailInfoBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }

        /* 문의하러가기 */
        binding.txtDetailInfoErrorSuggestions.setOnClickListener {
            SuggestionsDialog().show(supportFragmentManager, "SuggestionsDialog")
        }
    }

    private fun initVar() {
        /* document id 초기화 */
        id = intent.getStringExtra("categoryId")!!
        Log.d("TEST", "categoryId: $id")

        /* 선택한 항목 안내 초기화 */
        val category = intent.getStringExtra("category") ?: ""
        binding.txtDetailInfoTitle.text = category

        /* RecyclerView 초기화 */
        rvAdapter = DetailInfoAdapter()
        binding.rvDetailInfo.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(this@DetailInfoActivity)
        }

        getManualFromFireStore()
    }

    /* 매뉴얼 가져오기 */
    private fun getManualFromFireStore() {
        val db = FirebaseFirestore.getInstance()

        db.document("manuals/manual").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot = task.result

                    // null 처리
                    if (document.data == null) {
                        return@addOnCompleteListener
                    }

                    val data = mutableListOf<DetailInfoData>()
                    val list = document.data!!.get(id) as List<Any>

                    for (element in list) {
                        val hashMap = element as HashMap<String, String>
                        val title = hashMap.get("title")!!
                        val desc = hashMap.get("desc")!!

                        data.add(DetailInfoData(title, desc))
//                        Log.d("TEST", "매뉴얼 $title, $desc")
                    }

                    rvAdapter.updateData(data)
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFinishing) {
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }
    }
}