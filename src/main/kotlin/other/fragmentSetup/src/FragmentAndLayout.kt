import android.databinding.tool.ext.toCamelCase
import com.android.tools.idea.wizard.template.ProjectTemplateData

fun someFragment(
    packageName: String,
    entityName: String,
    graphQLData: String,
    graphQLDataDire: String,
    graphQLDataItem: String,
    projectData: ProjectTemplateData,
    collapsingToolbarLayout: Boolean,
    sharePlayFav: Boolean,
    listMedia: Boolean
): String {
    val emptyFragment = """package $packageName

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.koin.core.inject
import android.content.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import android.graphics.Color
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.joon.fm.R
import ${projectData.applicationPackage}.$graphQLDataDire.$graphQLData;
import com.joon.fm.core.base.BaseFragment
import com.joon.fm.core.base.genericRecycler.AppGenericAdapter
import com.joon.fm.core.base.genericRecycler.Section
import com.joon.fm.core.util.EndlessRecyclerViewScrollListener
import org.koin.core.KoinComponent
import androidx.fragment.app.viewModels
import com.joon.fm.databinding.Fragment${entityName}Binding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ${entityName}Fragment : BaseFragment<${entityName}ViewModel,Fragment${entityName}Binding>(),KoinComponent {
    override val viewModel: ${entityName}ViewModel by viewModel()
   
    
    @ExperimentalCoroutinesApi
    override fun onBindingCreated() {
    
    }
}
"""

    if (listMedia && !sharePlayFav && !collapsingToolbarLayout)
        return """package $packageName

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.koin.core.inject
import android.content.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import android.graphics.Color
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.joon.fm.R
import ${projectData.applicationPackage}.$graphQLDataDire.$graphQLData;
import com.joon.fm.core.base.BaseFragment
import com.joon.fm.core.base.genericRecycler.AppGenericAdapter
import com.joon.fm.core.base.genericRecycler.Section
import com.joon.fm.core.util.EndlessRecyclerViewScrollListener
import org.koin.core.KoinComponent
import androidx.fragment.app.viewModels
import com.joon.fm.databinding.Fragment${entityName}Binding
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.joon.fm.presentation.OnOpenMenuItem
import com.joon.fm.presentation.MoreOptionItems
import androidx.navigation.fragment.findNavController

class ${entityName}Fragment : BaseFragment<${entityName}ViewModel,Fragment${entityName}Binding>(),KoinComponent {
    override val viewModel: ${entityName}ViewModel by viewModel()
        
    var totalItems: Int = 0
    var page: Int = 2
    var count: Int = 0
    var itemsPerPage: Int = 30
    var canLoadMore: Boolean = true
    val context1: Context by inject()
        
    @ExperimentalCoroutinesApi
    private val adapter = AppGenericAdapter().apply {
        provider { context ->
            Item${entityName}(mainActivity, null, 4, object : OnOpenMenuItem {
                override fun openMenu(model: Any, index: Int) {
                    openBottomSheetMenu(index, model as ${entityName}Model)
                }

                override fun sendData(index: Int) {}

                override fun favoriteAction(index: Int, favorite: Boolean, id: Int) {
                    viewModel.favorite(index, id, favorite)
                }

                override fun play(index: Int) {
                    viewModel.playFromIndex(index, lastState)
                }
            })
        }
    }
    

    
    @ExperimentalCoroutinesApi
    fun openBottomSheetMenu(index: Int, model: ${entityName}Model) {
        initComponent(object : MoreOptionItems {
            override fun addToFavorite() {
                model.isFavorite?.let { model.id?.let { it1 -> viewModel.favorite(index, it1.toInt(), it) } }
            }

            override fun addToQueue() {
//                viewModel.addMediaToQueue(index,lastState)
            }

            override fun addToPlaylist() = findNavController().navigate(
                R.id.action_${entityName.decapitalize()}Fragment_to_playlistFragment,
                bundleOf("mediaId" to model.id)
            )

            override fun viewArtist() = findNavController().navigate(
                R.id.action_${entityName.decapitalize()}Fragment_to_artist, bundleOf(
                    "slug" to model.artist?.slug,
                    "id" to model.artist?.id,
                    "type" to model.entityType
                )
            )

            override fun viewAlbum() = findNavController().navigate(R.id.action_${entityName.decapitalize()}Fragment_to_singleAlbumsPageFragment,
                bundleOf(
                    "slug" to model.albums?.get(0)?.slug,
                    "image" to model.cover?.fullPath,
                    "name" to model.title,
                    "artist" to model.artist?.fullName
                ))

            override fun mediaDetails() = findNavController().navigate(
                R.id.action_${entityName.decapitalize()}Fragment_to_moreDetailsFragment,
                bundleOf(
                    "hash" to model.hash
                )
            )

            override fun share() = shareAction("")

        }) //, albumOption = model.albums != null)
    }
    
    @ExperimentalCoroutinesApi
    override fun onBindingCreated() {
        super.onBindingCreated()
        count = itemsPerPage
        val dataParts : Any? = null
        viewModel.getData(dataParts)
        
        val linearLayoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
        )
        binding.rcvMediaList${entityName}.adapter = adapter
        binding.rcvMediaList${entityName}.layoutManager = linearLayoutManager
        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if (scrollY >= v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight() && scrollY > oldScrollY) {
                    if (count < totalItems && canLoadMore) {

                        canLoadMore = false
                        val dataParts : Any? = null
                        viewModel.getData(dataParts)
                        count += itemsPerPage
                        page += 1

                    }
                }


            }
        })
    }
   
    @ExperimentalCoroutinesApi
    override fun registerObservers() = with(viewModel) {
        ${entityName.decapitalize()}ReceiverLiveData {
            val sections = ArrayList<Section<*>>().apply {
                for (item in it)
                    add(adapter.createSection<Item$entityName, $graphQLData.$graphQLDataItem>(item))
            }
            canLoadMore = true
            adapter.setSectionsDiffUtil(sections)
        }
    }
    
}
"""

    if(collapsingToolbarLayout && !sharePlayFav && !listMedia)
        return """package $packageName

import android.graphics.Color
import androidx.annotation.DrawableRes
import com.google.android.material.appbar.AppBarLayout
import com.joon.fm.R
import com.joon.fm.core.base.BaseFragment
import com.joon.fm.core.enums.CollapsingToolbarState
import com.joon.fm.core.enums.MediaType
import com.joon.fm.core.ext.setDrawable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.KoinComponent
import com.joon.fm.databinding.Fragment${entityName}Binding
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.joon.fm.core.enums.CollapsingToolbarState
import androidx.core.os.bundleOf

class ${entityName}Fragment : BaseFragment<${entityName}ViewModel, Fragment${entityName}Binding>(),
    KoinComponent {

    override val viewModel: ${entityName}ViewModel by viewModel()
    private var state: CollapsingToolbarState? = null

    @ExperimentalCoroutinesApi
    override fun onBindingCreated() {
        setupView()
    }

    private fun setupView() {
        state = CollapsingToolbarState.EXPANDED;
        binding.collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        binding.collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)
        binding.collapsingToolbar.title = ""
        binding.collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE)
        binding.collapsingToolbar.setExpandedTitleColor(Color.WHITE)
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                if (state != CollapsingToolbarState.EXPANDED) {
                    TODO("Not yet implemented")
                }
                state = CollapsingToolbarState.EXPANDED;
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (state != CollapsingToolbarState.COLLAPSED) {
                    TODO("Not yet implemented")
                }
                state = CollapsingToolbarState.COLLAPSED;
            } else {
                val percentScrollAppbar =
                    (70 * (0 - verticalOffset)) / appBarLayout.getTotalScrollRange()
                val percentScrollBack = (70 * percentScrollAppbar) / 100
                if (percentScrollBack < 50) binding.imgBack.translationY =
                    percentScrollBack.toFloat()
            }
        })


    }




    }"""

    if (collapsingToolbarLayout && sharePlayFav && listMedia)
        return """
            package $packageName

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.koin.core.inject
import android.content.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import android.graphics.Color
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.joon.fm.R
import ${projectData.applicationPackage}.$graphQLDataDire.$graphQLData;
import com.joon.fm.core.base.BaseFragment
import com.joon.fm.core.base.genericRecycler.AppGenericAdapter
import com.joon.fm.core.base.genericRecycler.Section
import com.joon.fm.core.util.EndlessRecyclerViewScrollListener
import org.koin.core.KoinComponent
import androidx.fragment.app.viewModels
import com.joon.fm.databinding.Fragment${entityName}Binding
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.joon.fm.presentation.OnOpenMenuItem
import com.joon.fm.presentation.MoreOptionItems
import androidx.navigation.fragment.findNavController
import com.joon.fm.core.enums.CollapsingToolbarState
import androidx.core.os.bundleOf

class ${entityName}Fragment : BaseFragment<${entityName}ViewModel,Fragment${entityName}Binding>(),KoinComponent {
    override val viewModel: ${entityName}ViewModel by viewModel()
    
    var totalItems: Int = 0
    var page: Int = 2
    var count: Int = 0
    var itemsPerPage: Int = 30
    var canLoadMore: Boolean = true
    val context1: Context by inject()
    private var state: CollapsingToolbarState? = null
    
    @ExperimentalCoroutinesApi
    private val adapter = AppGenericAdapter().apply {
        provider { context ->
            Item${entityName}(mainActivity, null, 4, object : OnOpenMenuItem {
                override fun openMenu(model: Any, index: Int) {
                    openBottomSheetMenu(index, model as ${entityName}Model)
                }

                override fun sendData(index: Int) {}

                override fun favoriteAction(index: Int, favorite: Boolean, id: Int) {
                    viewModel.favorite(index, id, favorite)
                }

                override fun play(index: Int) {
                    viewModel.playFromIndex(index, lastState)
                }
            })
        }
    }
    

    
    @ExperimentalCoroutinesApi
    fun openBottomSheetMenu(index: Int, model: ${entityName}Model) {
        initComponent(object : MoreOptionItems {
            override fun addToFavorite() {
                model.isFavorite?.let { model.id?.let { it1 -> viewModel.favorite(index, it1.toInt(), it) } }
            }

            override fun addToQueue() {
//                viewModel.addMediaToQueue(index,lastState)
            }

            override fun addToPlaylist() = findNavController().navigate(
                R.id.action_${entityName.decapitalize()}Fragment_to_playlistFragment,
                bundleOf("mediaId" to model.id)
            )

            override fun viewArtist() = findNavController().navigate(
                R.id.action_${entityName.decapitalize()}Fragment_to_artist, bundleOf(
                    "slug" to model.artist?.slug,
                    "id" to model.artist?.id,
                    "type" to model.entityType
                )
            )

            override fun viewAlbum() = findNavController().navigate(R.id.action_${entityName.decapitalize()}Fragment_to_singleAlbumsPageFragment,
                bundleOf(
                    "slug" to model.albums?.get(0)?.slug,
                    "image" to model.cover?.fullPath,
                    "name" to model.title,
                    "artist" to model.artist?.fullName
                ))

            override fun mediaDetails() = findNavController().navigate(
                R.id.action_${entityName.decapitalize()}Fragment_to_moreDetailsFragment,
                bundleOf(
                    "hash" to model.hash
                )
            )

            override fun share() = shareAction("")

        }) //, albumOption = model.albums != null)
    }
    

    
    @ExperimentalCoroutinesApi
    override fun onBindingCreated() {
        super.onBindingCreated()
        setupView()
        count = itemsPerPage
        val dataParts : Any? = null
        viewModel.getData(dataParts)
        
        val linearLayoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
        )
        binding.rcvMediaList${entityName}.adapter = adapter
        binding.rcvMediaList${entityName}.layoutManager = linearLayoutManager
        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if (scrollY >= v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight() && scrollY > oldScrollY) {
                    if (count < totalItems && canLoadMore) {

                        canLoadMore = false
                        val dataParts : Any? = null
                        viewModel.getData(dataParts)
                        count += itemsPerPage
                        page += 1

                    }
                }


            }
        })
    }
    
    private fun setupView() {
        state = CollapsingToolbarState.EXPANDED;
        binding.ctl${entityName}.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        binding.ctl${entityName}.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)
        binding.ctl${entityName}.title = getString()
        binding.ctl${entityName}.setCollapsedTitleTextColor(Color.WHITE)
        binding.ctl${entityName}.setExpandedTitleColor(Color.WHITE)
        binding.abl${entityName}.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                if (state != CollapsingToolbarState.EXPANDED) {
                    // CollapsingToolbarState.EXPANDED

                }
                state = CollapsingToolbarState.EXPANDED;
            } else if (Math.abs(verticalOffset) >= binding.abl${entityName}.getTotalScrollRange()) {
                if (state != CollapsingToolbarState.COLLAPSED) {
                    // COLLAPSED

                }

            } else {
                val percentScrollAppbar = (70 * (0 - verticalOffset)) / binding.abl${entityName}.getTotalScrollRange()
                val percentScrollBack = (70 * percentScrollAppbar) / 100
                // if (percentScrollBack < 50) binding.imgBack.translationY =
                   //     percentScrollBack.toFloat()
            }
        })


    }
    
    @ExperimentalCoroutinesApi
    override fun registerObservers() = with(viewModel) {
        ${entityName.decapitalize()}ReceiverLiveData {
            val sections = ArrayList<Section<*>>().apply {
                for (item in it)
                    add(adapter.createSection<Item$entityName, $graphQLData.$graphQLDataItem>(item))
            }
            canLoadMore = true
            adapter.setSectionsDiffUtil(sections)
        }
    }
    
    fun favMedia(){}
    
    fun playMedia(){}
    
}
"""

    if (!collapsingToolbarLayout && !sharePlayFav && !listMedia)
        return emptyFragment

    return emptyFragment
}

fun someFragmentLayout(
    packageName: String, entityName: String,
    collapsingToolbarLayout: Boolean,
    sharePlayFav: Boolean,
    listMedia: Boolean,
): String {
    val emptyFragment =  """
            <?xml version="1.0" encoding="utf-8"?>
            <layout xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:app="http://schemas.android.com/apk/res-auto"
                xmlns:tools="http://schemas.android.com/tools">

                <androidx.coordinatorlayout.widget.CoordinatorLayout
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:background="@color/colorBlack3"
                    app:statusBarBackground="@android:color/transparent">

                    <androidx.appcompat.widget.Toolbar
                        android:id="@+id/toolbarAlbum"
                        android:layout_width="match_parent"
                        android:layout_height="@dimen/toolbar_height"
                        android:elevation="0dp"
                        android:gravity="bottom|center"
                        app:contentInsetEnd="35dp"
                        app:contentInsetStart="0dp"
                        app:layout_collapseMode="pin"
                        app:titleTextColor="#fff">

                        <androidx.constraintlayout.widget.ConstraintLayout
                            android:layout_width="match_parent"
                            android:layout_marginStart="8dp"
                            android:layout_height="match_parent">

                            <com.joon.fm.core.customView.CustomTextView
                                android:id="@+id/txtCollection"
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:layout_marginStart="@dimen/size_62"
                                android:layout_marginBottom="@dimen/size_18"
                                android:text="@string/albums_name"
                                android:textSize="@dimen/title_page"
                                app:layout_constraintBottom_toBottomOf="parent"
                                app:layout_constraintStart_toStartOf="parent" />

                            <ImageView
                                android:id="@+id/img_back"
                                android:layout_width="@dimen/size_48"
                                android:padding="@dimen/size_16"
                                android:layout_height="@dimen/size_48"
                                android:layout_marginBottom="@dimen/size_6"
                                canBack="@{true}"
                                android:src="@drawable/ic_back_white"
                                app:layout_constraintBottom_toBottomOf="parent"
                                app:layout_constraintStart_toStartOf="parent" />

                        </androidx.constraintlayout.widget.ConstraintLayout>


                    </androidx.appcompat.widget.Toolbar>


                </androidx.coordinatorlayout.widget.CoordinatorLayout>

                <data>

                    <View />

                    <import type="android.view.View" />

                    <variable
                        name="vm"
                        type="${packageName}.${entityName}ViewModel" />

                    <variable
                        name="view"
                        type="$packageName.${entityName}Fragment" />

                </data>

            </layout>
        """.trimIndent()

    if (listMedia && !sharePlayFav && !collapsingToolbarLayout)
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <layout xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:app="http://schemas.android.com/apk/res-auto"
                xmlns:tools="http://schemas.android.com/tools">

                <androidx.coordinatorlayout.widget.CoordinatorLayout
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:background="@color/colorBlack3"
                    app:statusBarBackground="@android:color/transparent">

                    <androidx.appcompat.widget.Toolbar
                        android:id="@+id/toolbarAlbum"
                        android:layout_width="match_parent"
                        android:layout_height="@dimen/toolbar_height"
                        android:elevation="0dp"
                        android:gravity="bottom|center"
                        app:contentInsetEnd="35dp"
                        app:contentInsetStart="0dp"
                        app:layout_collapseMode="pin"
                        app:titleTextColor="#fff">

                        <androidx.constraintlayout.widget.ConstraintLayout
                            android:layout_width="match_parent"
                            android:layout_marginStart="8dp"
                            android:layout_height="match_parent">

                            <com.joon.fm.core.customView.CustomTextView
                                android:id="@+id/txt${entityName}"
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:layout_marginStart="@dimen/size_62"
                                android:layout_marginBottom="@dimen/size_18"
                                android:text="@string/albums_name"
                                android:textSize="@dimen/title_page"
                                app:layout_constraintBottom_toBottomOf="parent"
                                app:layout_constraintStart_toStartOf="parent" />

                            <ImageView
                                android:id="@+id/img_back"
                                android:layout_width="@dimen/size_48"
                                android:padding="@dimen/size_16"
                                android:layout_height="@dimen/size_48"
                                android:layout_marginBottom="@dimen/size_6"
                                canBack="@{true}"
                                android:src="@drawable/ic_back_white"
                                app:layout_constraintBottom_toBottomOf="parent"
                                app:layout_constraintStart_toStartOf="parent" />

                        </androidx.constraintlayout.widget.ConstraintLayout>


                    </androidx.appcompat.widget.Toolbar>

                    <androidx.core.widget.NestedScrollView
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:layout_marginTop="@dimen/toolbar_height"
                        android:id="@+id/nestedScrollView"
                        android:background="@color/colorBlack3"
                        android:orientation="vertical"
                        android:paddingTop="15dp"
                        app:layout_behavior="@string/appbar_scrolling_view_behavior">

                        <androidx.recyclerview.widget.RecyclerView
                            android:layout_marginStart="@dimen/size_16"
                            android:layout_marginEnd="@dimen/size_16"
                            android:id="@+id/rcvMedia${emptyFragment}"
                            android:layout_width="match_parent"
                            android:layout_height="match_parent"
                            android:orientation="vertical"
                            app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
                            app:layout_behavior="@string/appbar_scrolling_view_behavior" />

                    </androidx.core.widget.NestedScrollView>

                </androidx.coordinatorlayout.widget.CoordinatorLayout>

                <data>

                    <View />

                    <import type="android.view.View" />

                    <variable
                        name="vm"
                        type="${packageName}.${entityName}ViewModel" />

                    <variable
                        name="view"
                        type="$packageName.${entityName}Fragment" />

                </data>

            </layout>
        """.trimIndent()

    if (collapsingToolbarLayout && !listMedia && !sharePlayFav)
        return """<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <androidx.coordinatorlayout.widget.CoordinatorLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/colorBlack3"
        app:statusBarBackground="@android:color/transparent">

        <com.google.android.material.appbar.AppBarLayout
            android:id="@+id/appBarLayout"
            android:layout_width="match_parent"
            android:layout_height="@dimen/size_300"
            android:background="@null">

            <com.google.android.material.appbar.CollapsingToolbarLayout
                android:id="@+id/collapsing_toolbar"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@color/colorBlack3"
                app:collapsedTitleGravity="center|bottom"
                app:expandedTitleGravity="center|top"
                app:expandedTitleMarginTop="@dimen/size_37"
                app:layout_scrollFlags="scroll|exitUntilCollapsed"

                app:titleEnabled="true">

                <LinearLayout
                    android:id="@+id/layout_root"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:background="@drawable/bg_top_100_color"
                    android:orientation="horizontal"
                    app:layout_scrollFlags="scroll|enterAlways">


                    <ImageView
                        android:id="@+id/layout_bg"
                        android:layout_width="match_parent"
                        android:layout_height="@dimen/size_290"
                        android:layout_gravity="bottom"
                        android:src="@drawable/bg_top100" />


                </LinearLayout>


                <androidx.appcompat.widget.Toolbar
                    android:id="@+id/toolbar"
                    android:layout_width="match_parent"
                    android:layout_height="@dimen/toolbar_height"
                    android:elevation="0dp"
                    android:gravity="bottom|center"
                    app:contentInsetEnd="@dimen/size_35"
                    app:contentInsetStart="0dp"
                    app:layout_collapseMode="pin"
                    app:title="@string/top100"
                    app:titleTextColor="@android:color/white">

                    <androidx.appcompat.widget.AppCompatImageView
                        android:id="@+id/img_back"
                        android:layout_width="@dimen/size_48"
                        android:layout_height="@dimen/size_48"
                        android:layout_marginStart="@dimen/size_16"
                        android:layout_marginBottom="@dimen/size_6"
                        android:padding="@dimen/size_16"
                        android:src="@drawable/ic_back_white"
                        app:canBack="@{true}" />


                </androidx.appcompat.widget.Toolbar>

            </com.google.android.material.appbar.CollapsingToolbarLayout>

        </com.google.android.material.appbar.AppBarLayout>



    </androidx.coordinatorlayout.widget.CoordinatorLayout>

    <data>

        <View />

        <import type="android.view.View" />

        <variable
            name="vm"
            type="${packageName}.${entityName}ViewModel" />

        <variable
            name="view"
            type="$packageName.${entityName}Fragment" />

    </data>


</layout>
"""

    if (!collapsingToolbarLayout && !sharePlayFav && !listMedia) // blank fragment
        return emptyFragment

    if (collapsingToolbarLayout && sharePlayFav && listMedia)
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <layout xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:app="http://schemas.android.com/apk/res-auto">

                <androidx.coordinatorlayout.widget.CoordinatorLayout
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:background="@color/colorBlack3"
                    app:statusBarBackground="@android:color/transparent">

                    <com.google.android.material.appbar.AppBarLayout
                        android:id="@+id/abl${entityName}"
                        android:layout_width="match_parent"
                        android:layout_height="@dimen/size_300"
                        android:background="@null">

                        <com.google.android.material.appbar.CollapsingToolbarLayout
                            android:id="@+id/ctl${entityName}"
                            android:layout_width="match_parent"
                            android:layout_height="match_parent"
                            android:background="@color/colorBlack3"
                            app:collapsedTitleGravity="center|bottom"
                            app:expandedTitleGravity="center|top"
                            app:expandedTitleMarginTop="@dimen/size_37"
                            app:layout_scrollFlags="scroll|exitUntilCollapsed"
                            app:titleEnabled="true">

                            <LinearLayout
                                android:id="@+id/layout_root"
                                android:layout_width="match_parent"
                                android:layout_height="match_parent"
                                android:background="@drawable/bg_top_100_color"
                                android:orientation="horizontal"
                                app:layout_scrollFlags="scroll|enterAlways">


                                <ImageView
                                    android:id="@+id/layout_bg"
                                    android:layout_width="match_parent"
                                    android:layout_height="@dimen/size_290"
                                    android:layout_gravity="bottom"
                                    android:src="@drawable/bg_new_releases" />


                            </LinearLayout>

                            <androidx.appcompat.widget.Toolbar
                                android:id="@+id/toolbar"
                                android:layout_width="match_parent"
                                android:layout_height="@dimen/toolbar_height"
                                android:layout_marginBottom="@dimen/size_40"
                                android:elevation="0dp"
                                android:gravity="center"
                                app:contentInsetEnd="@dimen/size_35"
                                app:contentInsetStart="0dp"
                                app:layout_collapseMode="pin"
                                app:title="@string/new_releases"
                                app:titleTextColor="#fff">


                                <ImageView
                                    android:id="@+id/img_back"
                                    canBack="@{true}"
                                    android:layout_width="@dimen/size_48"
                                    android:layout_height="@dimen/size_48"
                                    android:layout_marginStart="@dimen/size_15"
                                    android:layout_marginBottom="@dimen/size_6"
                                    android:padding="@dimen/size_16"
                                    android:src="@drawable/ic_back_white" />


                            </androidx.appcompat.widget.Toolbar>
                        </com.google.android.material.appbar.CollapsingToolbarLayout>
                    </com.google.android.material.appbar.AppBarLayout>


                    <androidx.core.widget.NestedScrollView
                        android:id="@+id/nestedScrollView"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:background="@color/colorBlack3"
                        android:orientation="vertical"
                        android:paddingTop="5dp"
                        app:layout_behavior="@string/appbar_scrolling_view_behavior">

                        <LinearLayout
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:orientation="vertical">

                            <LinearLayout
                                android:layout_width="match_parent"
                                android:layout_height="@dimen/size_80">

                                <LinearLayout
                                    android:layout_width="match_parent"
                                    android:layout_height="wrap_content"
                                    android:layout_gravity="center"
                                    android:gravity="center">

                                    <androidx.cardview.widget.CardView
                                        isShare="@{true}"
                                        android:layout_width="@dimen/size_152"
                                        android:layout_height="70dp"
                                        android:layout_marginStart="@dimen/size_25"
                                        android:layout_marginEnd="@dimen/size_25"
                                        android:tag="http://nunu.bugloos.work/explore/new-releases"
                                        app:cardBackgroundColor="@color/colorBlack1"
                                        app:cardCornerRadius="@dimen/size_15"
                                        app:cardMaxElevation="0dp">

                                        <androidx.appcompat.widget.AppCompatImageView
                                            android:layout_width="@dimen/size_22"
                                            android:layout_height="@dimen/size_18"
                                            android:layout_gravity="center"
                                            android:gravity="center"
                                            android:src="@drawable/ic_share_white"
                                            android:textColor="@color/colorTextWhite"/>

                                    </androidx.cardview.widget.CardView>

                                    <androidx.cardview.widget.CardView
                                        android:id="@+id/card_play"
                                        android:layout_width="@dimen/size_152"
                                        android:layout_height="@dimen/size_70"
                                        android:layout_marginStart="@dimen/size_25"
                                        android:layout_marginEnd="@dimen/size_25"
                                        app:cardBackgroundColor="@color/colorBlack1"
                                        app:cardCornerRadius="@dimen/size_15"
                                        app:cardMaxElevation="0dp">

                                        <ImageView
                                            android:layout_width="@dimen/size_22"
                                            android:layout_height="@dimen/size_18"
                                            android:layout_gravity="center"
                                            android:src="@drawable/ic_play" />
                                    </androidx.cardview.widget.CardView>


                                </LinearLayout>


                            </LinearLayout>

                            <androidx.recyclerview.widget.RecyclerView
                                android:id="@+id/rcvMediaList${entityName}"
                                android:layout_width="match_parent"
                                android:layout_height="match_parent"
                                android:layout_marginBottom="@dimen/size_70"
                                android:orientation="vertical"
                                app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
                                app:layout_behavior="@string/appbar_scrolling_view_behavior" />
                        </LinearLayout>

                    </androidx.core.widget.NestedScrollView>

                </androidx.coordinatorlayout.widget.CoordinatorLayout>

                <data>

                    <View />

                    <import type="android.view.View" />

                    <variable
                        name="vm"
                        type="${packageName}.${entityName}ViewModel" />

                    <variable
                        name="view"
                        type="$packageName.${entityName}Fragment" />


                </data>


            </layout>

        """.trimIndent()

    return emptyFragment
}

fun someViewModel(packageName: String, entityName: String,graphQLData: String, listMedia:Boolean,graphQLDataDire: String):String{
    return if (listMedia)
        """package $packageName
        import com.joon.fm.$graphQLDataDire.$graphQLData
        import androidx.lifecycle.MutableLiveData
        import com.android.kotlinbaseproject.domain.usecase.base.UseCaseResponse
        import com.apollographql.apollo.api.Response
        import com.joon.fm.core.base.BaseViewModel
        import com.joon.fm.data.source.remote.ApiError
        import kotlinx.coroutines.ExperimentalCoroutinesApi
        import org.koin.core.KoinComponent
        import org.koin.core.inject
        
        
        // please add this line to di ( viewModel { ${entityName}ViewModel() } ) 
        class ${entityName}ViewModel : BaseViewModel(), KoinComponent {
            val ${entityName.decapitalize()}UseCase: ${entityName}UseCase by inject()
            var ${entityName.decapitalize()}ReceiverLiveData = MutableLiveData<ArrayList<${entityName}Model>>()


            fun getData(dataParts: String) {
                ${entityName.decapitalize()}UseCase.invoke(
                scope,
                dataParts,
                object : UseCaseResponse<Response<$graphQLData.Data>> {
                    override fun onSuccess(result: Response<$graphQLData.Data>) {
                        showProgressbar.value = true
                        setUpDataModel(result)
                    }

                    override fun onError(apiError: ApiError?) {
                        showProgressbar.postValue(false)
                    }

                })
            }
            
            private fun setUpDataModel(result: Response<$graphQLData.Data>) {
                val previous = ${entityName.decapitalize()}ReceiverLiveData.value ?: arrayListOf()
                var size = result.data?.mediaForYouWithPaginate?.data?.size
                for (i in 0 until size) {
                    var data = result.data?.mediaForYouWithPaginate?.data?.get(i);
                    if (data != null) {
                        previous.add(
                            ${entityName}Model(
                                id = data.id,
                                isFavorite = data.isFavorite,
                                isHide = data.isHide,
                                durationString = data.durationString,
                                isSponsored = data.isSponsored,
                                cover = data.cover,
                                artist = data.artist,
                                title = data.title,
                            )
                        )
                    }

                }
                ${entityName.decapitalize()}ReceiverLiveData.value = previous
            }
       }
    """
    else """
        package $packageName
        import androidx.lifecycle.MutableLiveData
                import com.android.kotlinbaseproject.domain.usecase.base.UseCaseResponse
                import com.apollographql.apollo.api.Response
                import com.joon.fm.core.base.BaseViewModel
                import com.joon.fm.data.source.remote.ApiError
                import kotlinx.coroutines.ExperimentalCoroutinesApi
                import org.koin.core.KoinComponent
                import org.koin.core.inject

                // please add this line to di ( viewModel {${entityName}ViewModel() } ) 
                class ${entityName}ViewModel : BaseViewModel(), KoinComponent {}
    """.trimIndent()
}

fun someUseCase(packageName: String, entityName: String,graphQLData: String,remoteApolloRepository:String
,graphQLDataDire:String):String{
    return """package $packageName
        import com.apollographql.apollo.api.Response
        import com.joon.fm.$graphQLDataDire.$graphQLData
        import com.android.kotlinbaseproject.domain.usecase.base.SingleUseCase
        import com.joon.fm.data.repository.${graphQLDataDire}.$remoteApolloRepository
        import ${packageName}.${entityName}Model
        
        // step 1
        // please add this line to di ( single { createGet${entityName}UseCase(get(),) } ) 
        
        // step 2
        /*
            fun ${entityName}UseCase(
                remoteRepository: $remoteApolloRepository
            ): ${entityName}UseCase {
                return ${entityName}UseCase(remoteRepository)
            }
        */
        
        // step  3
        class ${entityName}UseCase(private val remoteRepository: $remoteApolloRepository,
        ) : SingleUseCase<Response<$graphQLData.Data>, String>() {
                override suspend fun run(params: String?): Response<$graphQLData.Data> {
                    return remoteRepository.get${entityName}()
                }
        }
        
        // step 4
        // please add this code in ${graphQLDataDire}.${remoteApolloRepository}
        // suspend fun get${entityName}( params: String): Response<${graphQLData}.Data> {
            // return apolloClient.query(${graphQLData}(itemsPerPage = params)).await()
        // }
        
    """
}

fun someItem(packageName: String, entityName: String,graphQLData: String, graphQLDataDire:String):String{
    return """
        package $packageName

        import android.content.Context
        import android.util.AttributeSet
        import androidx.navigation.findNavController
        import com.joon.fm.R
        import com.joon.fm.presentation.OnOpenMenuItem
        import com.joon.fm.${graphQLDataDire}.${graphQLData}
        import com.joon.fm.core.base.BaseCustomView
        import com.joon.fm.core.base.genericRecycler.GenericAdapterView
        import com.joon.fm.databinding.Item${entityName}Binding
        import com.joon.fm.core.enums.EntityType

        class Item${entityName} @JvmOverloads constructor(
            context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,val onOpenMenuItem:OnOpenMenuItem
        ) : BaseCustomView<Item${entityName}Binding>(context, attrs, defStyleAttr),
            GenericAdapterView<${entityName}Model> {

            override fun onBind(model: ${entityName}Model,position: Int, extraObject: Any?) {
                binding.model = model
                // binding.position = position
                binding.root.setOnClickListener {
                    // findNavController().navigate(R.id.)
                    if(model.id != null){
                        if (model.entityType == EntityType.MEDIA.id)
                            onOpenMenuItem.play(model.id.toInt())
                    }
                }
                
                binding.imgLike.setOnClickListener {
                    onOpenMenuItem.favoriteAction(position,model.isFavorite,model.id.toInt())
                }
                
                binding.imgMoreOption.setOnClickListener {
                    onOpenMenuItem.openMenu(model,if (position >= 10) position - 1 else position)
                }
                
            }

        }

    """.trimIndent()
}

fun someItemLayout(
    packageName: String, entityName: String
): String {
    return  """<?xml version="1.0" encoding="utf-8"?>
<layout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto">



    <data>

        <import type="android.view.View" />

        <variable
            name="view"
            type="${packageName}.Item${entityName}" />

        <variable
            name="model"
            type="${packageName}.${entityName}Model" />

    </data>


    <LinearLayout
        android:layout_marginTop="@dimen/size_24"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:orientation="horizontal">

        <androidx.appcompat.widget.AppCompatImageView
            android:id="@+id/img_top_100_cover"
            loadImageUrl="@{model.cover.fullPath}"
            roundRadiusDp="@{12f}"
            android:layout_width="@dimen/size_46"
            android:layout_height="@dimen/size_46"
            android:layout_marginStart="@dimen/size_16"
            tools:src="@tools:sample/avatars" />

        <LinearLayout
            android:layout_marginEnd="@dimen/size_16"
            android:layout_marginStart="@dimen/size_14"
            android:layout_width="0dp"
            android:layout_weight="6"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:orientation="vertical">

            <com.joon.fm.core.customView.CustomTextView

                android:layout_width="match_parent"
                android:singleLine="true"
                android:ellipsize="end"
                android:layout_height="wrap_content"
                android:text="@{model.title}"
                android:textColor="#fff"
                android:textSize="@dimen/text_size_16"
                android:textStyle="bold"
                tools:text="The day you died" />

            <com.joon.fm.core.customView.CustomTextView


                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@{model.artist.fullName}"
                android:textColor="@color/colorTextDisabledButton"
                android:textSize="@dimen/text_size_14"
                tools:text="Arch Enemy" />

        </LinearLayout>



        <androidx.appcompat.widget.AppCompatImageView
            android:id="@+id/imgLike"
            android:layout_width="@dimen/size_48"
            android:layout_height="@dimen/size_48"
            android:layout_gravity="center"
            android:padding="@dimen/size_16"
            android:src="@{model.isFavorite ? @drawable/ic_like :@drawable/ic_like_withe}" />

        <androidx.appcompat.widget.AppCompatImageView
            android:layout_width="@dimen/size_48"
            android:layout_height="@dimen/size_48"
            android:layout_gravity="center"
            android:id="@+id/imgMoreOption"
            android:padding="@dimen/size_10"
            android:layout_marginEnd="@dimen/size_8"
            app:srcCompat="@drawable/dots_horizontal" />

    </LinearLayout>
</layout>
    """.trimIndent()
}


fun someItemModel(packageName: String, entityName: String, graphQLData: String,
                  graphQLDataItem: String, graphQLDataDire: String): String {

    return """
        package  $packageName
        import com.joon.fm.${graphQLDataDire}.${graphQLData}
        
        // insert GraphQL items  ${graphQLData}.${graphQLDataItem}
        data class ${entityName}Model()
    """.trimIndent()
}