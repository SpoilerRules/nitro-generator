package com.spoiligaming.generator

import com.spoiligaming.logging.Logger
import java.io.File

object ProxyHandler {
    private var proxyIndex = 0
    private var proxies: List<Pair<String, Int>> = emptyList()

    private fun loadProxies() {
        proxies = File("proxies.txt").readLines().map { line ->
            val (host, port) = line.split(":")
            Pair(host, port.toInt())
        }
    }

    fun getNextProxy(): Pair<String, Int>? {
        if (proxies.isEmpty() || proxyIndex >= proxies.size) {
            Logger.printWarning("All proxies are exhausted.")
            return null
        }

        val proxy = proxies[proxyIndex]
        proxyIndex++
        if (proxyIndex >= proxies.size) {
            proxyIndex = 0
        }
        return proxy
    }

    init {
        loadProxies()
    }
}