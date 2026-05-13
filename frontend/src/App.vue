<template>
  <n-config-provider :theme="darkTheme" :theme-overrides="themeOverrides" :locale="zhCN" :date-locale="dateZhCN">
    <div class="app-bg-layer"></div>
    <vue-particles
      id="global-particles"
      :options="globalParticleOptions"
      class="global-particles"
    />
    <n-message-provider>
      <n-dialog-provider>
        <n-notification-provider>
          <router-view v-slot="{ Component }">
            <transition name="page-fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </n-notification-provider>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { darkTheme, zhCN, dateZhCN } from 'naive-ui'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

const screenWidth = ref(window.innerWidth)

function onResize() {
  screenWidth.value = window.innerWidth
}

onMounted(() => {
  window.addEventListener('resize', onResize, { passive: true })
  auth.fetchMe()
})
onUnmounted(() => window.removeEventListener('resize', onResize))

const globalParticleOptions = {
  fullScreen: false,
  fpsLimit: 30,
  autoPlay: true,
  pauseOnBlur: true,
  particles: {
    number: {
      value: 70,
      density: { enable: true, area: 1000 },
    },
    color: {
      value: [
        '#e50914', '#e50914cc', '#ff9f1c', '#ff9f1ccc',
        '#ffffff', '#ffffffcc', '#ffffff99',
        '#ff4d4d', '#ffb74d',
      ],
    },
    shape: { type: 'circle' },
    opacity: {
      value: { min: 0.08, max: 0.3 },
      random: true,
      animation: {
        enable: true,
        speed: 0.35,
        minimumValue: 0.05,
        sync: false,
      },
    },
    size: {
      value: { min: 1, max: 4 },
      random: true,
      animation: {
        enable: true,
        speed: 2,
        minimumValue: 0.5,
        sync: false,
      },
    },
    links: {
      enable: true,
      distance: 150,
      color: '#e50914',
      opacity: 0.08,
      width: 0.8,
      triangles: { enable: false },
    },
    move: {
      enable: true,
      speed: { min: 0.2, max: 0.8 },
      direction: 'none',
      random: true,
      straight: false,
      outModes: { default: 'out' },
      gravity: {
        enable: true,
        acceleration: -0.35,
        inverse: false,
      },
      drift: { min: -0.2, max: 0.2 },
      vibrate: true,
      size: true,
    },
  },
  interactivity: {
    events: {
      onHover: {
        enable: true,
        mode: 'repulse',
        distance: 120,
      },
      onClick: { enable: false },
    },
    modes: {
      repulse: {
        distance: 120,
        duration: 0.4,
        speed: 6,
      },
    },
  },
  responsive: [
    {
      maxWidth: 992,
      options: {
        particles: {
          number: { value: 50, density: { enable: true, area: 1100 } },
          links: { opacity: 0.06 },
        },
      },
    },
    {
      maxWidth: 768,
      options: {
        particles: {
          number: { value: 35, density: { enable: true, area: 1200 } },
          size: { value: { min: 0.8, max: 3 } },
          move: { speed: { min: 0.15, max: 0.6 } },
          links: { opacity: 0.04, distance: 120 },
        },
        interactivity: {
          events: { onHover: { distance: 80 } },
          modes: { repulse: { distance: 80 } },
        },
      },
    },
    {
      maxWidth: 480,
      options: {
        particles: {
          number: { value: 22, density: { enable: true, area: 1400 } },
          size: { value: { min: 0.6, max: 2.5 } },
          move: { speed: { min: 0.1, max: 0.4 } },
          links: { enable: false },
        },
        interactivity: {
          events: { onHover: { enable: false } },
        },
      },
    },
  ],
  detectRetina: false,
}

const themeOverrides = {
  common: {
    primaryColor: '#e50914',
    primaryColorHover: '#ff0a16',
    primaryColorPressed: '#c40812',
    primaryColorSuppl: '#e50914',
    infoColor: '#ff9f1c',
    infoColorHover: '#ffb74d',
    infoColorPressed: '#e68900',
    infoColorSuppl: '#ff9f1c',
    successColor: '#81C784',
    successColorHover: '#43A047',
    successColorPressed: '#388E3C',
    successColorSuppl: '#81C784',
    warningColor: '#FFB74D',
    warningColorHover: '#ffb74d',
    warningColorPressed: '#e68900',
    warningColorSuppl: '#FFB74D',
    errorColor: '#e50914',
    errorColorHover: '#ff0a16',
    errorColorPressed: '#c40812',
    errorColorSuppl: '#e50914',
    bodyColor: '#121212',
    cardColor: '#1c1c1c',
    modalColor: '#1c1c1c',
    popoverColor: '#1c1c1c',
    inputColor: '#1c1c1c',
    tableColor: '#1c1c1c',
    tableColorHover: '#252525',
    tableHeaderColor: '#1c1c1c',
    borderColor: '#444444',
    dividerColor: '#444444',
    borderRadius: '8px',
    borderRadiusSmall: '4px',
    fontSize: '14px',
    fontSizeMini: '12px',
    textColorBase: '#ffffff',
    textColor1: '#ffffff',
    textColor2: '#ffffff',
    textColor3: '#e0e0e0',
    boxShadow1: '0 2px 8px rgba(0,0,0,0.4)',
    boxShadow2: '0 4px 16px rgba(0,0,0,0.5)',
    boxShadow3: '0 8px 30px rgba(0,0,0,0.55)',
  },
  Button: {
    borderRadiusMedium: '8px',
    borderRadiusSmall: '4px',
    textColor: '#ffffff',
    textColorPrimary: '#ffffff',
    textColorHoverPrimary: '#ffffff',
    textColorPressedPrimary: '#ffffff',
  },
  Card: {
    color: '#1c1c1c',
    borderColor: '#444444',
    borderRadius: '8px',
    textColor: '#ffffff',
    titleTextColor: '#ffffff',
    titleFontWeight: '600',
    padding: '20px',
  },
  Input: {
    color: '#1c1c1c',
    border: '#444444',
    borderHover: '#e50914',
    borderFocus: '#ffb74d',
    textColor: '#ffffff',
    placeholderColor: '#e0e0e0',
    borderRadius: '8px',
    heightMedium: '38px',
  },
  DataTable: {
    thColor: '#1c1c1c',
    tdColor: '#1c1c1c',
    tdColorStriped: '#1c1c1c',
    thTextColor: '#ffffff',
    tdTextColor: '#ffffff',
    borderColor: 'transparent',
    borderRadius: '8px',
    thFontWeight: '600',
  },
  Tag: {
    colorBorderedPrimary: 'rgba(229, 9, 20, 0.12)',
    textColorBorderedPrimary: '#e50914',
    borderPrimary: '#e50914',
  },
  Menu: {
    itemTextColor: '#e0e0e0',
    itemTextColorHover: '#ffffff',
    itemTextColorActive: '#e50914',
    itemTextColorChildActive: '#e50914',
    itemColorActive: 'rgba(229, 9, 20, 0.1)',
    itemColorActiveHover: 'rgba(229, 9, 20, 0.12)',
    itemIconColor: '#e0e0e0',
    itemIconColorHover: '#ffffff',
    itemIconColorActive: '#e50914',
    arrowColor: '#e0e0e0',
    arrowColorHover: '#ffffff',
    arrowColorActive: '#e50914',
    groupTextColor: '#e0e0e0',
    dividerColor: '#444444',
  },
  Pagination: {
    itemColor: '#1c1c1c',
    itemColorHover: '#252525',
    itemColorActive: '#e50914',
    itemTextColor: '#ffffff',
    itemTextColorActive: '#ffffff',
    itemBorder: '#444444',
    itemBorderHover: '#e50914',
    itemBorderActive: '#e50914',
  },
  Select: {
    peers: {
      InternalSelection: {
        color: '#1c1c1c',
        border: '#444444',
        borderHover: '#e50914',
        borderFocus: '#ffb74d',
        textColor: '#ffffff',
      }
    }
  },
}
</script>

<style>
.app-bg-layer {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 0;
  background: #121212;
  pointer-events: none;
}

.global-particles {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 1;
  pointer-events: none;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.3s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}
</style>
