import android.databinding.tool.ext.toCamelCase
import android.databinding.tool.ext.toCamelCaseAsVar
import org.apache.commons.lang.StringUtils

fun fragmentCode(entityName: String, graphQLData: String,
                 graphQLDataItem: String, graphQlLibraryName: String): String {
    return """
        // step 1

        // val dataParts: List<Int> = listOf(itemsPerPage, page)
        viewModel.get${entityName.capitalize()}(dataParts)

        // step 2

        private val adapter = AppGenericAdapter().apply {
            provider { context -> Section${entityName.capitalize()}Tracks(app) { navigateTo${entityName.capitalize()}() } }
        }

        // step 3

        private fun navigateTo${entityName.capitalize()}() {}

        // step 4

        override fun registerObservers() = with(viewModel) {
            ${entityName.decapitalize()}LiveData {
                val sections = ArrayList<Section<*>>().apply {
                    it.${entityName.decapitalize()}Tracks?.${graphQlLibraryName}?.data?.let { data ->
                        add(
                            adapter.createSection<Section${entityName}Tracks, List<${graphQLData}.${graphQLDataItem}>>(
                                data
                            )
                        )
                    }
                }

                adapter.setSectionsDiffUtil(sections)
            }
        }

    """.trimIndent()
}


fun viewModelCode(entityName: String, graphQLData: String,
                 graphQLDataItem: String, graphQlLibraryName: String): String {
    return """
        // step 1
        
        val get${entityName.capitalize()}TracksUseCase: Get${entityName}TracksUseCase by inject()
        var ${entityName.decapitalize()}ReceiverLiveData = MutableLiveData<ArrayList<${graphQLData}.${graphQLDataItem}>>()
        
        // step 2

        fun get${entityName.capitalize()}(dataParts: List<Int>) {
            get${entityName.capitalize()}TracksUseCase.invoke(
                scope,
                dataParts,
                object : UseCaseResponse<Response<${graphQLData}.Data>> {
                    override fun onSuccess(result: Response<${graphQLData}.Data>) {
                        showProgressbar.value = true
                        ${entityName.decapitalize()}ReceiverLiveData.value = 
                            result.data?.${graphQlLibraryName}?.data
                    }

                    override fun onError(apiError: ApiError?) {
                        showProgressbar.postValue(false)
                        // showJoonToast(apiError?.message!!,error = true)
                    }
                }
            )
        }

    """.trimIndent()
}

fun sectionFragmentCode(
    packageName: String,
    entityName: String,
    graphQLData: String,
    graphQLDataItem: String,
    graphQLDataDire: String,
): String {
    return """
        package $packageName

        import android.annotation.SuppressLint
        import android.content.Context
        import android.util.AttributeSet
        import com.joon.fm.core.base.BaseCustomView
        import com.joon.fm.core.base.genericRecycler.GenericAdapterView
        import com.joon.fm.core.base.genericRecycler.AppGenericAdapter
        import com.joon.fm.databinding.Section${graphQLDataDire.capitalize()}${entityName}TracksBinding
        import com.joon.fm.${graphQLDataDire}.${graphQLData}

        class Section${entityName}Tracks @JvmOverloads constructor(
            context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, val onClick:()->Unit
        ) : BaseCustomView<Section${graphQLDataDire.capitalize()}${entityName}TracksBinding>(context, attrs, defStyleAttr)
            , GenericAdapterView<List<${graphQLData}.${graphQLDataItem}>> {

            override fun onBind(model: List<${graphQLData}.${graphQLDataItem}>, position: Int, extraObject: Any?) {
                val adapter = AppGenericAdapter()
                binding.rcv${entityName}Tracks.adapter = adapter
                adapter.addSections<Item${entityName}Tracks, ${graphQLData}.${graphQLDataItem}>(model.subList(0,if(model.size > 6 ) 6 else model.size))
            }

        }
    """.trimIndent()
}




fun itemSection(
    packageName: String,
    entityName: String,
    graphQLDataDire: String,
    graphQLData: String,
    graphQLDataItem: String
):String{
    return """
        package $packageName

        import android.content.Context
        import android.util.AttributeSet
        import androidx.navigation.findNavController
        import com.joon.fm.R
        import com.joon.fm.${graphQLDataDire}.${graphQLData}
        import com.joon.fm.core.base.BaseCustomView
        import com.joon.fm.core.base.genericRecycler.GenericAdapterView
        import com.joon.fm.databinding.Item${graphQLDataDire.capitalize()}${entityName}TracksBinding

        class Item${entityName}Tracks @JvmOverloads constructor(
            context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
        ) : BaseCustomView<Item${graphQLDataDire.capitalize()}${entityName}TracksBinding>(context, attrs, defStyleAttr),
            GenericAdapterView<${graphQLData}.${graphQLDataItem}> {

            override fun onBind(model: ${graphQLData}.${graphQLDataItem},position: Int, extraObject: Any?) {
                binding.model = model
                // binding.position = position
                binding.root.setOnClickListener {
                    // findNavController().navigate(R.id.)
                }
            }

        }

    """.trimIndent()
}


fun useCaseSection(
    packageName: String,
    entityName: String,
    graphQLDataDire: String,
    graphQLData: String,
    remoteApolloRepository: String,
):String{
    return """
        package $packageName

        import com.android.kotlinbaseproject.domain.usecase.base.SingleUseCase
        import com.apollographql.apollo.api.Response
        import com.joon.fm.${graphQLDataDire}.${graphQLData}
        import com.joon.fm.data.repository.${graphQLDataDire}.${remoteApolloRepository}

        // step 1
        // please add this code in ${graphQLDataDire}.${remoteApolloRepository}
        // suspend fun get${entityName}Tracks( itemsPerPage: Int, page: Int): Response<${graphQLData}.Data> {
            // return apolloClient.query(${graphQLData}(itemsPerPage = itemsPerPage,page = page)).await()
        // }

        // step 2

        class Get${entityName}TracksUseCase(
            private val remoteRepository: ${remoteApolloRepository},
        ) : SingleUseCase<Response<${graphQLData}.Data>,  List<Int>>() {

            override suspend fun run(params: List<Int>?): Response<${graphQLData}.Data> {
                return remoteRepository.get${entityName}Tracks(params?.get(0)!!, params[1])
            }
        }

        // step 3
        // please add this code in di -> ${graphQLDataDire}Module
        // single { createGet${entityName}TracksUseCase(get(),) }
        // fun createGet${entityName}TracksUseCase(RemoteRepository: ${remoteApolloRepository}): Get${entityName}TracksUseCase {
        //     return Get${entityName}TracksUseCase(RemoteRepository)
        // }

    """.trimIndent()
}


fun sectionLayout(
    packageName: String,
    entityName: String,
    graphQLDataDire: String,
    graphQLData: String,
    graphQLDataItem: String
):String{
    return """<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

        <data>

            <import type="android.view.View" />

            <variable
                name="view"
                type="${packageName}.Section${entityName}Tracks" />
            
            <variable
                name="model"
                type="com.joon.fm.${graphQLDataDire}.${graphQLData}.${graphQLDataItem}" />

        </data>

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_marginTop="70dp"
                android:orientation="vertical">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal">

                    <com.joon.fm.core.customView.CustomTextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_marginBottom="@dimen/explore_title_section_bottom_margin"
                        android:text="${entityName}"
                        android:textSize="@dimen/text_size_20"
                        android:textStyle="bold" />

                    <View
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1" />

                    <com.joon.fm.core.customView.CustomTextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_marginEnd="@dimen/size_16"
                        android:onClick="@{v -> view.onClick.invoke()}"
                        android:text="@string/see_more"
                        android:textColor="@color/colorMOrange"
                        android:textSize="@dimen/text_size_14"/>

                </LinearLayout>

                <androidx.recyclerview.widget.RecyclerView
                    android:id="@+id/rcv${entityName}Tracks"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal"
                    app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />

            </LinearLayout>


    </layout>
    """.trimIndent()
}



fun itemLayout(
    packageName: String,
    entityName: String,
    graphQLDataDire: String,
    graphQLData: String,
    graphQLDataItem: String
):String{
    return """<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

        <data>

            <import type="android.view.View" />

            <variable
                name="view"
                type="${packageName}.Item${entityName}Tracks" />

            <variable
                name="model"
                type="com.joon.fm.${graphQLDataDire}.${graphQLData}.${graphQLDataItem}" />

        </data>

        <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:tools="http://schemas.android.com/tools"
            android:layout_width="wrap_content"
            android:layout_marginEnd="@dimen/size_16"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <androidx.constraintlayout.widget.ConstraintLayout
                android:id="@+id/root"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent"
                android:layout_width="@dimen/size_96"
                android:layout_height="@dimen/size_96">

                <androidx.appcompat.widget.AppCompatImageView
                    android:id="@+id/img_top_100_cover"
                    loadImageUrl="@{model.cover.fullPath}"
                    roundRadiusDp="@{20f}"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    tools:src="@tools:sample/avatars" />

            </androidx.constraintlayout.widget.ConstraintLayout>


            <com.joon.fm.core.customView.CustomTextView
                app:layout_constraintTop_toBottomOf="@+id/root"
                app:layout_constraintEnd_toEndOf="@+id/root"
                android:id="@+id/txt_song_name"
                android:layout_width="0dp"
                android:gravity="start"
                android:layout_height="wrap_content"
                app:layout_constraintStart_toStartOf="@+id/root"
                android:layout_marginTop="@dimen/size_12"
                android:text="@{model.title}"
                android:textSize="@dimen/text_size_16"
                tools:text="test"/>

        </androidx.constraintlayout.widget.ConstraintLayout>


    </layout>
    """.trimIndent()
}

