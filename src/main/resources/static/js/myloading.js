(function ($) {
    var methods = {
        show: function () {
            return this.each(function (i) {
                var _this = $(this);
                if (_this.css('position') != 'absolute' && _this.css('position') != 'relative') {
                    _this.css('position', 'relative');
                }
                _this.prepend('<div class="myloading"><span class="glyphicon glyphicon-refresh"></span></div>');
            });
        },
        hide: function () {
            return this.each(function (i) {
                var _this = $(this);
                _this.find('.myloading').remove();
            });
        }
    }
    $.fn.myloading = function (options) {
        var method = arguments[0];
        if (methods[method]) {
            method = methods[method];
        } else if (!method) {
            method = methods.show;
        } else {
            $.error('error');
            return this;
        }
        return method.apply(this, arguments);
    }
})(jQuery)