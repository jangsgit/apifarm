let GridUtil = {
    changeOrder: function (val, $grid) {
        // val : U,D
        let _this = this;
        if ($grid.getList("selected").length == 0) {
            Alert.alert('', '순서를 변경할 Row 를 선택하세요.');
            return;
        }

        if (val == "U") {
            let items = $grid.getList("selected");
            //for (let i = 0; i < selected.length; i++) {
            $.each(items, function (index, self_item) {
                let self_index = self_item.__index;
                let upper_index = self_index - 1;
                if (upper_index == - 1) {
                    Alert.alert('', "첫번째 Row가 선택되었습니다.");
                    return;
                }
                let upper_item = $grid.list[upper_index];

                upper_item.__index = self_index;
                self_item.__index = upper_index;

                $grid.updateRow(self_item, upper_index);
                $grid.updateRow(upper_item, self_index);
                $grid.select(self_index, { selected: false });
                $grid.select(upper_index, { selected: true });
            });
        } else {
            let items = $grid.getList("selected");
            items.reverse(); // 아래로 내릴 때는 역순으로 루프를 돌려야 한다.
            $.each(items, function (index, self_item) {
                let self_index = self_item.__index;
                let under_index = self_index + 1;
                if ($grid.list.length <= under_index) {
                    Alert.alert('', "마지막 Row가 선택되었습니다.");
                    return;
                }
                let under_item = $grid.list[under_index];

                under_item.__index = self_index;
                self_item.__index = under_index;

                $grid.updateRow(self_item, under_index);
                $grid.updateRow(under_item, self_index);

                $grid.select(self_index, { selected: false });
                $grid.select(under_index, { selected: true });


            });
        }
    },
    adjustHeight: function (grid_config, rows_len) {
    //let grid_config = _this.grid_config;
    let height = grid_config.header.columnHeight + 3 + (grid_config.body.columnHeight + 3) * (rows_len + 2);
    if (height < 150)
        height = 150;

    grid_config.target.css('height', height + 'px');
    },
};