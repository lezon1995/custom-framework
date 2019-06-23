<template>

  <div>
    <div>

      <el-form :inline="true" :model="form" class="demo-form-inline">
        <el-form-item label="昵称">
          <el-input v-model="form.name" placeholder="请输入您的昵称呢"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login">进入聊天室</el-button>
        </el-form-item>
      </el-form>
    </div>


  </div>

</template>

<script>

  export default {
    data() {
      return {
        path: "ws://localhost:8888/ims",
        socket: "",
        form: {
          name: ''
        },

      }
    },
    mounted() {
      // 初始化
      this.init()
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
        alert(msg.data)
        this.$router.push({
          name: 'Chatroom',
          params: {
            socket: this.socket,
            name: this.form.name
          }
        })
      },
      login() {
        // alert(123)
        this.socket.send(`[LOGIN][1561270093000][${this.form.name}]`)
      },
      send(msg) {
        this.socket.send(msg)
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
            resolve([{
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
                }]
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
              [{
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
                }]
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

</style>
