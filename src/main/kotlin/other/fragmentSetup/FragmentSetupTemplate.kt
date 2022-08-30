package other.fragmentSetup

import com.android.tools.idea.wizard.template.*
import other.sectionSetup.getGraphQlDir

private const val MIN_SDK = 21

@ExperimentalStdlibApi
val fragmentSetupTemplate
    get() = template {
        revision = 2
        name = "Bugloos ( Create Fragment for show data )"
        description = "Creates a new fragment along layout file."
        minApi = MIN_SDK
        minBuildApi = MIN_SDK
        category = Category.Fragment
        formFactor = FormFactor.Mobile
        screens = listOf(
            WizardUiContext.FragmentGallery,
            WizardUiContext.MenuEntry,
            WizardUiContext.NewProject,
            WizardUiContext.NewModule
        )

        val packageNameParam = defaultPackageNameParameter

        val entityName = stringParameter {
            name = "Entity Name"
            default = "Test"
            help = "The name of the entity class to create and use in Fragment"
            constraints = listOf(Constraint.NONEMPTY)
        }

        val remoteApolloRepository = stringParameter {
            name = "remote Apollo Repository"
            default = "HomeRemoteApolloRepository"
            help = "The name of the remote Apollo Repository to create for Use Case"
        }

        val graphQLData = stringParameter {
            name = "graphQL Data Name"
            default = "home.GetPlaylistItemQuery"
            help = "The name of the graphQL (only name)"
        }

        val graphQLDataItem = stringParameter {
            name = "graphQL Data Resources"
            default = "Medium"
            help = "The name of the graphQL Data Resources (only name)"
        }

        val collapsingToolbarLayout = booleanParameter {
            name = "collapsing Toolbar Layout"
            default = false
            help = " Add collapsing toolbar layout to layout"
        }

        val sharePlayFav = booleanParameter {
            name = "Add share , play , Fav icons "
            default = false
            help = " Add collapsing toolbar layout to layout"
        }

        val listMedia = booleanParameter {
            name = "Add list for show data "
            default = false
            help = "if you want to show data in list fill checkbox"
        }




        widgets(
            TextFieldWidget(entityName),
//            TextFieldWidget(layoutName),
            TextFieldWidget(graphQLData),
            TextFieldWidget(graphQLDataItem),
            TextFieldWidget(remoteApolloRepository),

            PackageNameWidget(packageNameParam),
            CheckBoxWidget(collapsingToolbarLayout),
            CheckBoxWidget(sharePlayFav),
            CheckBoxWidget(listMedia)

        )

        recipe = { data: TemplateData ->
//            fragmentSetup(
//                data as ModuleTemplateData,
//                packageNameParam.value,
//                entityName.value,
//
//                remoteApolloRepository.value,
//                graphQLData.value,
//                graphQLDataItem.value,
//
//                collapsingToolbarLayout.value,
//                sharePlayFav.value,
//                listMedia.value
//            )
            var collapsingToolbarLayoutRules = collapsingToolbarLayout.value
            if (sharePlayFav.value)
                collapsingToolbarLayoutRules = true

            var listMediaRules = listMedia.value
            if (collapsingToolbarLayoutRules && sharePlayFav.value)
                listMediaRules = true

            val graphQl = getGraphQlDir(graphQLData.value)
            fragmentSetup(
                moduleData = data as ModuleTemplateData,
                packageName = packageNameParam.value,
                entityName = entityName.value,

                graphQLDataItem = graphQLDataItem.value,
                graphQLDataDire = graphQl[0],
                graphQLData = graphQl[1],
                remoteApolloRepository = remoteApolloRepository.value,

                collapsingToolbarLayout = collapsingToolbarLayoutRules,
                sharePlayFav = sharePlayFav.value,
                listMedia = listMediaRules
            )
        }
    }

val defaultPackageNameParameter
    get() = stringParameter {
        name = "Package name"
        visible = { !isNewModule }
        default = "com.mycompany.myapp"
        constraints = listOf(Constraint.PACKAGE)
        suggest = { packageName }
    }

