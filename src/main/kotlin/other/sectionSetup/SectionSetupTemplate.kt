package other.sectionSetup

import com.android.tools.idea.wizard.template.*

private const val MIN_SDK = 21

@ExperimentalStdlibApi
val sectionSetupTemplate
    get() = template {
        revision = 3
        name = "Bugloos ( Create Section Fragment for show data )"
        description = "Creates a new Section fragment."
        minApi = MIN_SDK
        minBuildApi = MIN_SDK
        category = Category.Fragment
        formFactor = FormFactor.Mobile
        screens = listOf(
            WizardUiContext.FragmentGallery, WizardUiContext.MenuEntry,
            WizardUiContext.NewProject, WizardUiContext.NewModule
        )

        val packageNameParam = defaultPackageNameParameter2

        val entityName = stringParameter {
            name = "Entity Name"
            default = "PlaylistForYou"
            help = "The name of the entity class to create and use in Fragment"
            constraints = listOf(Constraint.NONEMPTY)
        }

        val graphQLData = stringParameter {
            name = "graphQL Data Name"
            default = "home.GetLibraryForYouTracksQuery.Data1"
            help = "The name of the graphQL (only name)"
        }

//        val graphQLDataItem = stringParameter {
//            name = "graphQL Data Resources"
//            default = "Data"
//            help = "The name of the graphQL Data Resources (only name)"
//        }

        val graphQlLibraryName = stringParameter {
            name = "graphQL Library Name"
            default = "libraryForYou"
            help = "The name of the graphQL Library Name (only name)"
            validate()
        }

//        val graphQLDataDire = stringParameter {
//            name = "graphQL Data Dire"
//            default = "artist"
//            help = "The name of the graphQL Data Dire (only name)"
//        }

        val remoteApolloRepository = stringParameter {
            name = "Remote Apollo Repository"
            default = "HomeRemoteApolloRepository"
            help = "The name of the Remote Apollo Repository (only name)"
        }



        widgets(
            TextFieldWidget(entityName),
            TextFieldWidget(graphQLData),
//            TextFieldWidget(graphQLDataItem),
            TextFieldWidget(graphQlLibraryName),
//            TextFieldWidget(graphQLDataDire),
            TextFieldWidget(remoteApolloRepository),
            PackageNameWidget(packageNameParam),

            )

        recipe = { data: TemplateData ->

            val graphQl = getGraphQlDir(graphQLData.value)
            sectionFragmentSetup(
                moduleData = data as ModuleTemplateData,
                packageName = packageNameParam.value,
                entityName = entityName.value,
                graphQlLibraryName = graphQlLibraryName.value,
                graphQLDataDire = graphQl[0],
                graphQLData = graphQl[1],
                graphQLDataItem = graphQl[2],
                remoteApolloRepository = remoteApolloRepository.value,
            )
        }


        //        val layoutName = stringParameter {
//            name = "Layout Name"
//            default = "fragment_test"
//            help = "The name of the layout to create for the fragment"
//            constraints = listOf(Constraint.LAYOUT, Constraint.UNIQUE, Constraint.NONEMPTY)
//        }
    }

val defaultPackageNameParameter2
    get() = stringParameter {
        name = "Package name2"
        visible = { !isNewModule }
        default = "com.mycompany.myapp2"
        constraints = listOf(Constraint.PACKAGE)
        suggest = { packageName }
    }

fun getGraphQlDir(ql: String): List<String> {
    return if (!ql.contains(".")) listOf(ql)
    else ql.split(".")
}