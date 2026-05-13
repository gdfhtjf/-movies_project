import { createApp } from 'vue'
import { createPinia } from 'pinia'
import naive from 'naive-ui'
import Particles from '@tsparticles/vue3'
import { loadBasic } from '@tsparticles/basic'
import { loadParticlesLinksInteraction } from '@tsparticles/interaction-particles-links'
import { loadExternalRepulseInteraction } from '@tsparticles/interaction-external-repulse'
import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(naive)

app.use(Particles, {
  init: async (engine) => {
    await loadBasic(engine)
    await loadParticlesLinksInteraction(engine)
    await loadExternalRepulseInteraction(engine)
  }
})

app.mount('#app')
