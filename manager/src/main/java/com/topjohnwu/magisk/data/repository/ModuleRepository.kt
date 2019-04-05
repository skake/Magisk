package com.topjohnwu.magisk.data.repository

import android.content.Context
import com.topjohnwu.magisk.data.network.GithubApiServices
import com.topjohnwu.magisk.data.network.GithubRawApiServices
import com.topjohnwu.magisk.data.network.GithubServices
import com.topjohnwu.magisk.model.entity.GithubRepo
import com.topjohnwu.magisk.model.entity.toRepository
import com.topjohnwu.magisk.util.writeToFile
import com.topjohnwu.magisk.util.writeToString
import io.reactivex.Single


class ModuleRepository(
    private val context: Context,
    private val apiRaw: GithubRawApiServices,
    private val api: GithubApiServices,
    private val apiWeb: GithubServices
) {

    fun fetchModules() = fetchAllRepos()
        .flattenAsFlowable { it }
        .flatMapSingle { fetchProperties(it.name, it.updatedAtMillis) }
        .toList()

    fun fetchInstallFile(module: String) = apiRaw
        .fetchFile(module, FILE_INSTALL_SH)
        .map { it.writeToFile(context, FILE_INSTALL_SH) }

    fun fetchReadme(module: String) = apiRaw
        .fetchFile(module, FILE_README_MD)
        .map { it.writeToString() }

    fun fetchConfig(module: String) = apiRaw
        .fetchFile(module, FILE_CONFIG_SH)
        .map { it.writeToFile(context, FILE_CONFIG_SH) }

    fun fetchInstallZip(module: String) = apiWeb
        .fetchModuleZip(module)
        .map { it.writeToFile(context, FILE_INSTALL_ZIP) }


    private fun fetchProperties(module: String, lastChanged: Long) = apiRaw
        .fetchFile(module, "module.prop")
        .map { it.toRepository(lastChanged) }

    private fun fetchAllRepos(page: Int = 0): Single<List<GithubRepo>> = api.fetchRepos(page)
        .flatMap {
            if (it.size == GithubApiServices.REPOS_PER_PAGE) {
                fetchAllRepos(page + 1).map { newList -> it + newList }
            } else {
                Single.just(it)
            }
        }

    companion object {
        const val FILE_INSTALL_SH = "install.sh"
        const val FILE_README_MD = "README.md"
        const val FILE_CONFIG_SH = "config.sh"
        const val FILE_INSTALL_ZIP = "install.zip"
    }

}
