package com.plum.pgyer.plugin.command

/**
 * 执行Git命令
 */
class GitCommand {

    /**
     * 获取最近的一条提交记录
     * @return
     */
    static def getOneRecentLog() {
        return 'git log  -1  --pretty=format:%s --abbrev-commit'.execute().text.trim()
    }

    /**
     * 获取当前打包分支
     * @return
     */
    static def getCurrentBranch() {
        return 'git symbolic-ref --short HEAD'.execute().text.trim()
    }

    /**
     * 获取git的用户名
     * @return
     */
    static def getUploadUserName() {
        return 'git config user.name'.execute().text.trim()
    }
}