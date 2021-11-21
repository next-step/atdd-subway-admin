import {SET_LINE, SET_LINES} from '@/store/shared/mutationTypes'
import {
    CREATE_LINE,
    CREATE_SECTION,
    DELETE_LINE,
    DELETE_SECTION,
    EDIT_LINE,
    FETCH_LINE,
    FETCH_LINES
} from '@/store/shared/actionTypes'
import LineService from '@/api/modules/line'

const state = {
    line: {},
    lines: []
}

const getters = {
    line(state) {
        return state.line
    },
    lines(state) {
        return state.lines
    }
}

const mutations = {
    [SET_LINE](state, line) {
        state.line = line
    },
    [SET_LINES](state, lines) {
        state.lines = lines
    }
}

const actions = {
    async [CREATE_LINE]({commit}, newLine) {
        return LineService.create(newLine)
    },
    async [FETCH_LINE]({commit}, lineId) {
        return LineService.get(lineId).then(({data}) => {
            commit(SET_LINE, data)
            return data
        })
    },
    async [FETCH_LINES]({commit}) {
        return LineService.getAll().then(({data}) => {
            commit(SET_LINES, data)
        })
    },
    async [EDIT_LINE]({commit}, editingLine) {
        return LineService.update(editingLine)
    },
    async [DELETE_LINE]({commit}, lineId) {
        return LineService.delete(lineId)
    },
    async [DELETE_SECTION]({commit}, {lineId, stationId}) {
        return LineService.deleteSection({lineId, stationId})
    },
    async [CREATE_SECTION]({commit}, {lineId, section}) {
        return LineService.createSection({lineId, section})
    }
}

export default {
    state,
    getters,
    actions,
    mutations
}
