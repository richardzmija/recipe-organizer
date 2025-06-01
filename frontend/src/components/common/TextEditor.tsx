import { useEditor, EditorContent } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import Placeholder from '@tiptap/extension-placeholder';
import { Box, Button, HStack } from '@chakra-ui/react';
import { useColorModeValue } from '@/components/ui/color-mode';

interface Props {
  value: string;
  onChange: (content: string) => void;
  placeholder?: string;
  width?: string | number;
  height?: string | number;
}

export default function TextEditor({ value, onChange, placeholder, width = '100%', height = '120px' }: Props) {
  const editor = useEditor({
    extensions: [
      StarterKit.configure({ bulletList: false }),
      Placeholder.configure({
        placeholder: placeholder ?? '',
        showOnlyWhenEditable: true,
        showOnlyCurrent: false,
      }),
    ],
    editorProps: {
      attributes: { class: 'tiptap-editor', style: 'height:100%; padding: 0.5rem; overflow-y: auto;' },
    },
    content: value,
    onUpdate: ({ editor }) => onChange(editor.getHTML()),
  });

  const activeBg = useColorModeValue('gray.200', 'gray.600');
  const inactiveBg = 'transparent';
  const activeColor = useColorModeValue('black', 'white');
  const inactiveColor = useColorModeValue('gray.600', 'gray.400');
  const borderColor = useColorModeValue('gray.300', 'gray.600');

  return (
    <Box
      border='1px solid'
      borderColor={borderColor}
      borderRadius='md'
      w={width}
      h={height}
      display='flex'
      flexDirection='column'
      overflow='hidden'>
      <Box p={2} display='flex' flexDirection='column' flex='1'>
        {editor && (
          <HStack gap={2} mb={2} flexWrap='wrap'>
            <Button
              size='xs'
              onClick={() => editor.chain().focus().toggleBold().run()}
              fontWeight='bold'
              bg={editor.isActive('bold') ? activeBg : inactiveBg}
              color={editor.isActive('bold') ? activeColor : inactiveColor}
              _hover={{ bg: activeBg }}>
              Bold
            </Button>
            <Button
              size='xs'
              onClick={() => editor.chain().focus().toggleItalic().run()}
              fontStyle='italic'
              bg={editor.isActive('italic') ? activeBg : inactiveBg}
              color={editor.isActive('italic') ? activeColor : inactiveColor}
              _hover={{ bg: activeBg }}>
              Italic
            </Button>
          </HStack>
        )}

        <Box flex='1'>
          <EditorContent editor={editor} style={{ width: '100%', height: '100%', outline: 'none' }} />
        </Box>
      </Box>
    </Box>
  );
}
