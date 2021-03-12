package com.example

import com.change_vision.jude.api.inf.model.IDiagram
import com.change_vision.jude.api.inf.presentation.INodePresentation
import java.awt.Point
import javax.swing.JButton

enum class RefreshBaseType(val label: String) {
    IMAGE("Refresh based on Image"),
    NOTE("Refresh based on Note")
}

class ButtonRefresh(private val type: RefreshBaseType) : JButton()  {
    init {
        text = type.label
    }

    fun push(diagram: IDiagram?) {
        if (diagram == null)
            return
        val allNodePresentation = diagram.presentations.filterIsInstance<INodePresentation>()
        val allNotes = allNodePresentation.filter { it.type == "Note" }
        val allImages = allNodePresentation.filter { it.type == "Image" }

        allNotes.forEach { note ->
            val storedRelation: String? = AstahAccessor.readTaggedValue(note.model, IMAGE_KEY)
            val storedImageIDs = if (storedRelation != null) {
                JsonSaveDataConverter.convertFromJsonToImages(storedRelation)
            } else {
                listOf()
            }
            val storedImages = mutableListOf<INodePresentation>()
            storedImageIDs.forEach { id ->
                val associatedImage = allImages.firstOrNull { image -> image.id == id }
                if (associatedImage != null) {
                    storedImages.add(associatedImage)
                }
            }
            if (storedImages.isNotEmpty()) {
                val minX = storedImages.minBy { it.location.x }!!.location.x
                val minY = storedImages.minBy { it.location.y }!!.location.y
                val maxXElement = storedImages.maxBy { it.location.x + it.width }!!
                val maxX = maxXElement.location.x + maxXElement.width
                val maxYElement = storedImages.maxBy { it.location.y + it.height }!!
                val maxY = maxYElement.location.y + maxYElement.height

                when (type) {
                    RefreshBaseType.NOTE -> {
                        storedImages.forEach { image ->
                            val newImageLocation = Point()
                            newImageLocation.setLocation(
                                    note.location.x + (image.location.x - minX) + 10,
                                    note.location.y + (image.location.y - minY) + 10)
                            AstahAccessor.setLocation(image, newImageLocation)
                        }
                    }
                    RefreshBaseType.IMAGE -> {
                        val newNoteLocation = Point()
                        newNoteLocation.setLocation(minX - 10, minY - 10)
                        AstahAccessor.setLocation(note, newNoteLocation)
                    }
                }
                AstahAccessor.setSize(note, maxX - minX + 20, maxY - minY + 20)
            }
        }
    }
}