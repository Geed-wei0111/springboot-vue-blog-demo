import {createStore} from 'vuex'

export default createStore({
    state: {
        currentPathName: '',//当前页面路径的名字
        currentPath: '',//当前页面路径
        menuState: false,//菜单是否展开，false为展开，true为折叠
    },
    mutations: {
        setPath(state){//保存路径在store里
          state.currentPathName = localStorage.getItem("currentPathName");
          state.currentPath = localStorage.getItem("currentPath");
        },
        changeMenuState(state){//改变菜单的展开状态
            state.menuState = !state.menuState;
        },
        setMenuCollapse(state){
            state.menuState = true;
        }
    },
    actions: {},
    modules: {}
})