export const PAGES_MENU = [
  {
    
    path: 'pages',
    children: [
        
      {
        path: 'home',
        data: {
          menu: {
            title: 'general.menu.home',
            icon: 'ion-gear-a',
            selected: false,
            expanded: false,
            order: 250,
          },
        },
        children: [
          {
            path: 'treeview',
            data: {
              menu: {
                title: 'general.menu.learning_home',
              },
            },
          },
           {
            path: 'learningsimulator',
            data: {
              menu: {
                title: 'general.menu.learning_simulator',
              },
            },
          },
        ],
      },
    ],
  },
];
