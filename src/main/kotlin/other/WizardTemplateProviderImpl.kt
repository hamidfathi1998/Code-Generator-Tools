package other

import com.android.tools.idea.wizard.template.Template
import com.android.tools.idea.wizard.template.WizardTemplateProvider
import other.fragmentSetup.fragmentSetupTemplate
import other.sectionSetup.sectionSetupTemplate

class WizardTemplateProviderImpl : WizardTemplateProvider() {

    @ExperimentalStdlibApi
    override fun getTemplates(): List<Template> = listOf(fragmentSetupTemplate, sectionSetupTemplate)
}
