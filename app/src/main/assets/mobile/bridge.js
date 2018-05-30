
window._mobile = {
  dialog: {
    close: function() {
      window.Android ? window.Android.close() : location.href = 'uyun-close://'
    }
  },
  map: {
    open: function(data) {
      window.Android ? window.Android.openMap(data) : location.href = 'uyun-openMap://' + encodeURIComponent(data)
    },
    transLocationData: function(data) {
      return window._getLocationData(JSON.parse(data))
    }
  },
  print: {
    open: function(data) {
      window.Android ? window.Android.openPrint(data) : ''
    }
  },

  headerChange: function(data) {
    window.Android ? window.Android.headerChange(data) : location.href = 'uyun-headerChange://' + encodeURIComponent(data)
  },

  screenRotation: function(data) {
    window.Android ? window.Android.screenRotation(data) : location.href = 'uyun-screenrotation://' + encodeURIComponent(data)
  },

  close: function() {
    window.Android ? window.Android.close() : location.href = 'uyun-closeforshow://'
  }
}
