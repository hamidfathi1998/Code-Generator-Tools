package other.fragmentSetup

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.impl.activities.common.addAllKotlinDependencies
import org.apache.commons.lang.StringUtils
import someFragment
import someFragmentLayout
import someItem
import someItemLayout
import someItemModel
import someUseCase
import someViewModel

@ExperimentalStdlibApi
fun RecipeExecutor.fragmentSetup(
    moduleData: ModuleTemplateData,
    packageName: String,
    entityName: String,
    remoteApolloRepository: String,
    graphQLDataDire: String,
    graphQLData: String,
    graphQLDataItem: String,
    collapsingToolbarLayout: Boolean,
    sharePlayFav: Boolean,
    listMedia: Boolean,
) {
    val (projectData, srcOut, resOut) = moduleData

    addAllKotlinDependencies(moduleData)

    val sectionName = StringUtils.splitByCharacterTypeCamelCase(entityName)

    var layoutName = ""
    for (sc in sectionName)
        layoutName += "_" + sc.decapitalize()


//    // This will generate new manifest (with activity) to merge it with existing
//    generateManifest(moduleData, activityClass, activityTitle, packageName,
//            isLauncher = false, hasNoActionBar = true, generateActivityTitle = true)

    if (listMedia) {
        save(
            someUseCase(
                packageName = packageName, entityName = entityName, graphQLData = graphQLData,
                remoteApolloRepository = remoteApolloRepository, graphQLDataDire = graphQLDataDire
            ),
            srcOut.resolve("${entityName}UseCase.kt")
        )

        save(
            someItem(
                packageName = packageName,
                entityName = entityName,
                graphQLData = graphQLData,
                graphQLDataDire = graphQLDataDire
            ),
            srcOut.resolve("Item${entityName}.kt")
        )

        save(
            someItemModel(
                packageName = packageName, entityName = entityName, graphQLData = graphQLData,
                graphQLDataItem = graphQLDataItem, graphQLDataDire = graphQLDataDire
            ),
            srcOut.resolve("${entityName}Model.kt")
        )

        save(
            someItemLayout(
                packageName = packageName, entityName = entityName,
            ),
            resOut.resolve("layout/item_${entityName.lowercase()}.xml")
        )
    }

    save(
        someFragment(
            packageName = packageName,
            entityName = entityName,
            graphQLData = graphQLData,
            graphQLDataItem = graphQLDataItem,
            projectData = projectData,
            collapsingToolbarLayout = collapsingToolbarLayout,
            sharePlayFav = sharePlayFav,
            listMedia = listMedia, graphQLDataDire = graphQLDataDire
        ),
        srcOut.resolve("${entityName}Fragment.kt")
    )

    save(
        someViewModel(
            packageName = packageName, entityName = entityName,
            graphQLData = graphQLData, listMedia = listMedia,graphQLDataDire=graphQLDataDire
        ),
        srcOut.resolve("${entityName}ViewModel.kt")
    )

    save(
        someFragmentLayout(
            packageName,
            entityName,
            collapsingToolbarLayout,
            sharePlayFav,
            listMedia
        ),
        resOut.resolve("layout/fragment$layoutName.xml")
    )

}


