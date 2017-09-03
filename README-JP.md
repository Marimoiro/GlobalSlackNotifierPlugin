# 使い方


1. SlackPluginで投稿できる状態にする
2. GlobalSlack<Messagesの設定をする

# GlobalSlackMessagesの設定
通知したいステータスに合わせて通知メッセージを作成します。

例えばSUCCESSの時に通知したい場合
* Success messageにメッセージ書きます。（この時Jenkinsの環境変数を${BUILD_URL}のように利用することができます。)
* 必要であればchannel notifying of success buildｍに通知先のChannelを書きます。（書かない場合、Slack Pluginのものが使用されます。)
* notify on build successにチェックを入れます

これで、何かビルドが成功するたびにSlackに自動的にメッセージが流れます。（ジョブに何か設定する必要は一切ありません。）
