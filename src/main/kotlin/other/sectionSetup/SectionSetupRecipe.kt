package other.sectionSetup

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.impl.activities.common.addAllKotlinDependencies
import fragmentCode
import itemLayout
import itemSection
import sectionFragmentCode
import sectionLayout
import useCaseSection
import viewModelCode
import org.apache.commons.lang.StringUtils

@ExperimentalStdlibApi
fun RecipeExecutor.sectionFragmentSetup(
    moduleData: ModuleTemplateData,
    packageName: String,
    entityName: String,
    graphQlLibraryName: String,
    graphQLData: String,
    graphQLDataItem: String,
    graphQLDataDire: String,
    remoteApolloRepository: String
) {
    val (projectData, srcOut, resOut) = moduleData
    addAllKotlinDependencies(moduleData)

    val sectionName = StringUtils.splitByCharacterTypeCamelCase(entityName)

    var layoutName = ""
    graphQLDataDire?.let {
        layoutName = it
    }
    for (sc in sectionName)
        layoutName += "_" + sc.decapitalize()


    save(
        fragmentCode(
            entityName = entityName,
            graphQLData = graphQLData,
            graphQLDataItem = graphQLDataItem,
            graphQlLibraryName = graphQlLibraryName
        ),
        srcOut.resolve("${entityName}Fragment.txt")
    )

    save(
        viewModelCode(
            entityName = entityName,
            graphQLData = graphQLData,
            graphQLDataItem = graphQLDataItem,
            graphQlLibraryName = graphQlLibraryName
        ),
        srcOut.resolve("${entityName}ViewModel.txt")
    )

    save(
        sectionFragmentCode(
            packageName = packageName,
            entityName = entityName,
            graphQLData = graphQLData,
            graphQLDataItem = graphQLDataItem,
            graphQLDataDire = graphQLDataDire),
        srcOut.resolve("section${entityName}Tracks.kt")
    )

    save(
        itemSection(
            packageName = packageName,
            entityName = entityName,
            graphQLData = graphQLData,
            graphQLDataItem = graphQLDataItem,
            graphQLDataDire = graphQLDataDire,
        ),
        srcOut.resolve("Item${entityName}Tracks.kt")
    )

    save(
        useCaseSection(
            packageName= packageName,
            entityName= entityName,
            graphQLDataDire= graphQLDataDire,
            graphQLData= graphQLData,
            remoteApolloRepository= remoteApolloRepository,
        ),
        srcOut.resolve("Get${entityName}TracksUseCase.kt")
    )

    save(
        sectionLayout(
            packageName = packageName,
            entityName= entityName,
            graphQLDataDire= graphQLDataDire,
            graphQLData= graphQLData,
            graphQLDataItem= graphQLDataItem
        ),
        resOut.resolve("layout/section_${layoutName}_tracks.xml")
    )

    save(
        itemLayout(
            packageName= packageName,
            entityName= entityName,
            graphQLDataDire= graphQLDataDire,
            graphQLData= graphQLData,
            graphQLDataItem= graphQLDataItem
        ),
        resOut.resolve("layout/item_${layoutName}_tracks.xml")
    )

}