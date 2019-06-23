<template>

  <div>
    <wxChat ref="wxchat"
            :data="wxChatData"
            :showShade="false"
            contactNickname="群聊"
            :getUpperData="getUpperData"
            :getUnderData="getUnderData"
            :sendMsg="sendMsg"
            :ownerAvatarUrl="ownerAvatarUrl"
            :contactAvatarUrl="contactAvatarUrl"
            :width="800">
    </wxChat>
    <div class="sendBox">
      <el-form :inline="true" style="alignment: center">
        <el-form-item>
          <el-input  type="text" placeholder="请输入消息" v-model="textMessage" style="width: 700px"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="sendMsg">发送</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>

</template>

<script>
  import wxChat from './wxChat.vue'
  import Main from './Main.vue'

  export default {
    data() {
      return {
        regx: /^\[(.*)\](\s\-\s(.*))?/g,
        socket: {},
        name: '',
        upperTimes: 0,
        underTimes: 0,
        upperId: 0,
        underId: 6,
        ownerAvatarUrl: './src/assets/avatar1.png',
        contactAvatarUrl: './src/assets/avatar2.png',
        textMessage: '',
        wxChatData: []
      }
    },
    components: {wxChat},
    mounted() {
      this.socket = this.$route.params.socket;
      this.name = this.$route.params.name;
      // 监听socket消息
      this.socket.onmessage = this.getMessage;
    },
    methods: {
      init() {
        if (typeof (WebSocket) === "undefined") {
          alert("您的浏览器不支持socket")
        } else {
          // 实例化socket
          this.socket = new WebSocket(this.path)
          // 监听socket连接
          this.socket.onopen = this.open;
          // 监听socket错误信息
          this.socket.onerror = this.error
          // 监听socket消息
          this.socket.onmessage = this.getMessage
        }
      },
      open() {
        console.log("socket连接成功")
      },
      error() {
        console.log("连接错误")
      },
      getMessage(msg) {
        let group;
        let label;
        let content;
        while (group = this.regx.exec(msg.data)) {
          label = group[1];
          content = group[3];
        }
        this.$refs.wxchat.addData({
          direction: 1,
          id: 3,
          type: 1,
          content: content,
          name: label.split(/\]\[/)[2],
          ctime: new Date().toLocaleString()
        })
      },

      sendMsg() {
        /**
         * 在@link wxChatData 新增一条记录
         */
        let that = this;
        this.$refs.wxchat.addData({
          direction: 2,
          id: 3,
          type: 1,
          content: that.textMessage,
          name: this.name,
          ctime: new Date().toLocaleString()
        })
        this.socket.send(`[CHAT][1561270093000][${this.name}] - ${this.textMessage}`)
      },
      close() {
        console.log("socket已经关闭")
      },


      //向上滚动加载数据
      getUpperData() {
        let me = this;
        // 这里为模拟异步加载数据
        // 实际上你可能要这么写:
        // return axios.get('xxx').then(function(result){
        //     return result;  //result的格式需要类似下面resolve里面的数组
        // })
        return new Promise(function (resolve) {
          setTimeout(function () {
            //模拟加载完毕
            if (me.upperTimes > 3) {
              return resolve([]);
            }
            //加载数据
            resolve([/*{
                direction: 2,
                id: me.upperId - 1,
                type: 1,
                content: '向上滚动加载第 ' + me.upperTimes + ' 条！',
                ctime: new Date().toLocaleString()
              },
                {
                  direction: 1,
                  id: me.upperId - 2,
                  type: 1,
                  content: '向上滚动加载第 ' + me.upperTimes + ' 条！',
                  ctime: new Date().toLocaleString()
                }*/]
            )
          }, 1000);
          me.upperId = me.upperId + 2;
          me.upperTimes++;
        })
      },

      getUnderData() {
        let me = this;
        //意义同getUpperData()
        return new Promise(function (resolve) {
          setTimeout(function () {
            //模拟加载完毕
            if (me.underTimes > 3) {
              return resolve([]);
            }
            //加载数据
            resolve(
              [/*{
                direction: 1,
                id: me.underId + 1,
                type: 1,
                content: '向下滚动加载第 ' + me.underTimes + ' 条！',
                ctime: new Date().toLocaleString()
              },
                {
                  direction: 2,
                  id: me.underId + 2,
                  type: 1,
                  content: '向下滚动加载第 ' + me.underTimes + ' 条！',
                  ctime: new Date().toLocaleString()
                }*/]
            )
          }, 1000);
          me.underId = me.underId + 2;
          me.underTimes++;
        })
      }
    },
    destroyed() {
      // 销毁监听
      this.socket.onclose = this.close
    }
  }
</script>

<style>
  * {
    margin: 0;
    padding: 0;
  }

  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin-top: 60px;
  }

  h1, h2 {
    font-weight: normal;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }

  li {
    display: inline-block;
  }

  .sendBox {
    margin-top: 22.5%;
    bottom: 10px;
    overflow: hidden;
    padding: 0;
    height: 100%;
    position: relative;
    z-index: 101;
  }

</style>
