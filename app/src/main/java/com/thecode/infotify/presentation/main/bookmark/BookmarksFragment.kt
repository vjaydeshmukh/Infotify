package com.thecode.infotify.presentation.main.bookmark

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.thecode.infotify.BuildConfig
import com.thecode.infotify.R
import com.thecode.infotify.core.domain.Article
import com.thecode.infotify.databinding.FragmentBookmarksBinding
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import org.json.JSONException

/**
 * A simple [Fragment] subclass.
 */
class BookmarksFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: BookmarkRecyclerViewAdapter
    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var layoutEmptyState: LinearLayout
    private lateinit var listArticles: ArrayList<Article>
    val realm: Realm = Realm.getDefaultInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)

        val view = binding.root
        refreshLayout = binding.refreshLayout
        recyclerView = binding.recyclerViewNewsBookmark
        layoutEmptyState = binding.layoutBookmarkEmpty
        recyclerAdapter = BookmarkRecyclerViewAdapter(view.context)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        // recyclerView.adapter = recyclerAdapter
        recyclerView.adapter = SlideInBottomAnimationAdapter(recyclerAdapter)

        refreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorPrimary,
            R.color.colorPrimaryDark,
            R.color.colorPrimaryDark
        )
        val typedValue = TypedValue()
        val theme: Resources.Theme = view.context.theme
        theme.resolveAttribute(R.attr.primaryCardBackgroundColor, typedValue, true)
        @ColorInt val color = typedValue.data
        refreshLayout.setProgressBackgroundColorSchemeColor(color)
        refreshLayout.setOnRefreshListener {
            displayBookmarks(listArticles)
        }

        realm.refresh()

        listArticles = ArrayList()
        val query: RealmQuery<Article> = realm.where(Article::class.java)
        val results: RealmResults<Article> = query.findAll()
        var i: Int
        if (results.isNotEmpty()) {
            layoutEmptyState.visibility = View.GONE
            i = 0
            while (i < results.size) {
                if (BuildConfig.DEBUG && results[i] == null) {
                    error("Assertion failed")
                }
                listArticles.add(i, results[i]!!)
                i++
            }
        } else {
            layoutEmptyState.visibility = View.VISIBLE
        }

        displayBookmarks(listArticles)

        return view
    }

    private fun displayBookmarks(articles: ArrayList<Article>) {
        try {
            val articleArrayList: ArrayList<Article> = ArrayList()
            for (i in articles.indices) {
                val article = articles[i]
                articleArrayList.add(article)
                recyclerAdapter.setArticleListItems(articleArrayList)
            }
            refreshLayout.isRefreshing = false
            recyclerView.scheduleLayoutAnimation()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}