package com.spoiligaming.generator

import com.spoiligaming.generator.gui.Initializer
import com.spoiligaming.logging.Logger
import javafx.application.Application

fun main(args: Array<String>) {
    Logger.showDebug = "-debug" in args
    GeneratorBean.fakeValidation = "-showcase" in args
    Application.launch(Initializer::class.java)
   /* NitroValidationWrapper.alertWebhook("test", true)
    NitroValidationWrapper.alertWebhook("test", false)
    NitroValidationWrapper.alertWebhook("test", null)*/
}