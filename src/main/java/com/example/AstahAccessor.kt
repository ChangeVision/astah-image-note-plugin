package com.example

import com.change_vision.jude.api.inf.AstahAPI
import com.change_vision.jude.api.inf.editor.TransactionManager
import com.change_vision.jude.api.inf.exception.InvalidEditingException
import com.change_vision.jude.api.inf.model.*
import com.change_vision.jude.api.inf.presentation.INodePresentation
import com.change_vision.jude.api.inf.project.ProjectAccessor
import java.awt.geom.Point2D
import java.lang.StringBuilder

const val white = "#FFFFFF"

object AstahAccessor {
    private val projectAccessor: ProjectAccessor = AstahAPI.getAstahAPI().projectAccessor

    fun writeTaggedValueInDefinition(element: IElement, key : String , value: String) {
        if (element !is INamedElement)
            throw error("")

        val otherDefinitions = (element.definition ?: "").split(System.getProperty("line.separator")).filter {
            !it.startsWith("#$key")
        }
        val definition = StringBuilder()

        otherDefinitions.forEach {
            definition.append(it)
            definition.append(System.getProperty("line.separator"))
        }
        definition.append("#$key = $value")

        try {
            TransactionManager.beginTransaction()
            element.definition = definition.toString()
            TransactionManager.endTransaction()
        } catch (e : Exception) {
            e.printStackTrace()
            if (TransactionManager.isInTransaction()) {
                TransactionManager.abortTransaction()
            }
        }
    }

    fun readTaggedValueInDefinition(element: IElement, key : String) : String? {
        if (element !is INamedElement)
            throw error("")

        return (element.definition ?: "").split(System.getProperty("line.separator")).firstOrNull() {
            it.startsWith("#$key")
        }?.removePrefix("#$key = ")
    }

    fun setLocation(node: INodePresentation, location: Point2D) {
        try {
            TransactionManager.beginTransaction()
            node.location = location
            TransactionManager.endTransaction()
        } catch (e : Exception) {
            e.printStackTrace()
            if (TransactionManager.isInTransaction()) {
                TransactionManager.abortTransaction()
            }
        }
    }

    fun setSize(node: INodePresentation, width: Double, height: Double) {
        try {
            TransactionManager.beginTransaction()
            node.width = width
            node.height = height
            TransactionManager.endTransaction()
        } catch (e : Exception) {
            e.printStackTrace()
            if (TransactionManager.isInTransaction()) {
                TransactionManager.abortTransaction()
            }
        }
    }

    fun writeTaggedValue(element: IElement, tagKey : String , json: String) {
        try {
            TransactionManager.beginTransaction()
            val tag = getTaggedValue(element, tagKey, true)
            tag!!.value = json
            TransactionManager.endTransaction()
        } catch (e : Exception) {
            e.printStackTrace()
            if (TransactionManager.isInTransaction()) {
                TransactionManager.abortTransaction()
            }
        }
    }

    fun readTaggedValue(element: IElement, tagKey : String) : String? {
        try {
            getTaggedValue(element, tagKey, false)?.let { tag ->
                return tag.value
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getTaggedValue(element: IElement, tagKey: String, isCreateIfNotExist: Boolean) : ITaggedValue? {
        var ret: ITaggedValue? = null
        val isInTransaction = TransactionManager.isInTransaction()
        try {
            ret = element.taggedValues.firstOrNull { it.key.equals(tagKey, ignoreCase = true) }
            if (ret == null && isCreateIfNotExist) {
                if (!isInTransaction)
                    TransactionManager.beginTransaction()
                ret = if (projectAccessor.astahEdition.toLowerCase() == "sysml")
                    projectAccessor.modelEditorFactory.sysmlModelEditor.createTaggedValue(element, tagKey, "")
                else
                    projectAccessor.modelEditorFactory.basicModelEditor.createTaggedValue(element, tagKey, "")
                if (!isInTransaction)
                    TransactionManager.endTransaction()
            }
        } catch (exp: Exception) {
            exp.printStackTrace()
            if (!isInTransaction && TransactionManager.isInTransaction()) {
                TransactionManager.abortTransaction()
            }
        }
        return ret
    }


    fun getCurrentDiagram(): IDiagram? =
            projectAccessor.viewManager.diagramViewManager.currentDiagram

    fun setColor(ps: INodePresentation, color: String) {
        try {
            TransactionManager.beginTransaction()
            ps.setProperty("fill.color", color)
            TransactionManager.endTransaction()
        } catch (e: InvalidEditingException) {
            e.printStackTrace()
            TransactionManager.abortTransaction()
        }
    }
}