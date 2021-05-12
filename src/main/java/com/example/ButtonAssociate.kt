package com.example

import com.change_vision.jude.api.inf.model.IDiagram
import com.change_vision.jude.api.inf.presentation.INodePresentation
import javax.swing.JButton

class ButtonAssociate : JButton("Associate") {
    fun push(diagram: IDiagram?) {
        if (diagram == null)
            return
        val allNodePresentation = diagram.presentations.filterIsInstance<INodePresentation>()
        val allNotes = allNodePresentation.filter { it.type == "Note" || it.type == "Comment" }
        val allImages = allNodePresentation.filter { it.type == "Image" }
        val currentRelations = mutableMapOf<INodePresentation, List<INodePresentation>>()
        val storedRelations = mutableMapOf<INodePresentation, List<String>>()
        val updatedRelations = mutableMapOf<INodePresentation, Set<String>>()
        allNotes.forEach { note ->
            val containedImages = mutableListOf<INodePresentation>()
            containedImages.addAll(allImages.filter { image -> isCompletelyOverlapped(note, image) })
            currentRelations[note] = containedImages
        }
        allNotes.forEach { note ->
            val storedRelation = AstahAccessor.readTaggedValueInDefinition(note.model, IMAGE_KEY)
            if (storedRelation != null) {
                storedRelations[note] = JsonSaveDataConverter.convertFromJsonToImages(storedRelation)
            } else {
                storedRelations[note] = mutableListOf()
            }
            val updateRelation = mutableSetOf<String>()
            updateRelation.addAll(storedRelations[note]!!)
            updateRelation.addAll(currentRelations[note]!!.map { it.id })
            if (updateRelation.isNotEmpty()) {
                AstahAccessor.setColor(note, white)
                updatedRelations[note] = updateRelation
                AstahAccessor.writeTaggedValueInDefinition(note.model, IMAGE_KEY,
                        JsonSaveDataConverter.convertFromImagesToJSON(updateRelation.toTypedArray()))
            }
        }
    }

    private fun isCompletelyOverlapped(note: INodePresentation, image: INodePresentation): Boolean =
            note.rectangle.contains(image.rectangle)
}
